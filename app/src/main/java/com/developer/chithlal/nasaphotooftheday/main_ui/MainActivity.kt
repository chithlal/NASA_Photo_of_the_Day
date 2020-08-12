package com.developer.chithlal.nasaphotooftheday.main_ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.developer.chithlal.nasaphotooftheday.R
import com.developer.chithlal.nasaphotooftheday.application.App
import com.developer.chithlal.nasaphotooftheday.databinding.ActivityMainBinding
import com.developer.chithlal.nasaphotooftheday.util.ConnectivityUtil
import com.developer.chithlal.nasaphotooftheday.util.Constants
import com.developer.chithlal.nasaphotooftheday.util.Constants.BUTTON_TYPE
import com.developer.chithlal.nasaphotooftheday.util.Constants.BUTTON_TYPE.*
import com.developer.chithlal.nasaphotooftheday.util.FullScreenHelper
import com.developer.chithlal.nasaphotooftheday.util.UrlParser
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainContract.View,View.OnTouchListener {
    lateinit var animHideToTop: Animation
    lateinit var animShowFromTop: Animation
    lateinit var animHideToBottom: Animation
    lateinit var animShowFromBottom: Animation
    private var isDatePickerOpen: Boolean = false
    private val BACKGROUND_ALPHA: Float = 0.6F
    private lateinit var mBinding: ActivityMainBinding

    @Inject
    lateinit var mPresenter: MainContract.Presenter
    lateinit var selectedDate: String
    lateinit var mYouTubePlayer: YouTubePlayer
    lateinit var mYouTubePlayerView: YouTubePlayerView
    lateinit var lastMediaType: BUTTON_TYPE
    lateinit var fullScreenHelper: FullScreenHelper
    var isFullScreenOn: Boolean = false
    var selecedDay: Int = 0
    var selecedMonth: Int = 0
    var selecedYear: Int = 0
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    var mScaleFactor: Float = 1.0f
    var mImageView: android.widget.ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        //Injecting presenter
        (application as App).appComponent.inject(this)

        mYouTubePlayerView = findViewById(R.id.youtube_player_view)
        initAnimation()
        mPresenter.lodUi(this)

        fullScreenHelper = FullScreenHelper(this) //helper class to enable full screen mode
        initViews()

        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener()) // To provide zooming feature for imageview
        setupListeners()

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Toggle fullscreen mode when screen orientation changes
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isFullScreenOn)
                fullScreenHelper.toggleHideyBar()

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isFullScreenOn)
                fullScreenHelper.toggleHideyBar()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(Constants.IS_FULLSCREEN, isFullScreenOn)
        outState.putString(Constants.TEXT_DESCRIPTION, mBinding.tvDescription.text.toString())
        outState.putString(Constants.TEXT_TITLE, mBinding.title.text.toString())
        outState.putString(Constants.IMAGE_URL, mPresenter.imageUrl)
        outState.putString(Constants.LAST_MEDIA_TYPE, lastMediaType.name)


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isFullScreenOn = savedInstanceState.getBoolean(Constants.IS_FULLSCREEN, isFullScreenOn)
        mBinding.tvDescription.text = savedInstanceState.getString(
            Constants.TEXT_DESCRIPTION,
            mBinding.tvDescription.text.toString()
        )
        mBinding.title.text =
            savedInstanceState.getString(Constants.TEXT_TITLE, mBinding.title.text.toString())
        mPresenter.imageUrl = savedInstanceState.getString(Constants.IMAGE_URL, mPresenter.imageUrl)
        lastMediaType =
            valueOf(savedInstanceState.getString(Constants.LAST_MEDIA_TYPE, ""))
    }



    override fun setTitle(title: String) {
        mBinding.title.text = title
    }

    override fun setDescription(description: String) {
        mBinding.tvDescription.movementMethod = ScrollingMovementMethod()
        mBinding.tvDescription.text = description
    }

    override fun setImage(imageUrl: String) {
        setBackgroundImage(imageUrl)
    }

    override fun setVideo(videoUrl: String) {

        mYouTubePlayerView.visibility = View.VISIBLE
        val mLifecycle = lifecycle
        //Youtube player listener, When the player init completed and ready to use - return player object
        mYouTubePlayerView.getYouTubePlayerWhenReady(youTubePlayerCallback = object :
            YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                mYouTubePlayer = youTubePlayer
                val vId = UrlParser.getVideoId(videoUrl)
                mYouTubePlayer.loadOrCueVideo(mLifecycle, vId, 0F)
                mYouTubePlayerView.enterFullScreen()

            }

        })


    }

    override fun setupButton(type: BUTTON_TYPE) {
        when (type) {
            IMAGE -> setupImageFab()
            VIDEO -> setupVideoFab()
            CANCEL -> setupCancelFab()
        }

    }

    private fun setupCancelFab() {
        mBinding.fab.hide()
        GlobalScope.launch {
            delay(500L)
        }
        mBinding.fab.setImageResource(R.drawable.ic_round_close_24)
        mBinding.fab.contentDescription = Constants.BUTTON_TYPE_CANCEL_STR
        mBinding.fab.show()
    }

    private fun setupVideoFab() {
        if (mYouTubePlayerView.isFullScreen()) {
            mYouTubePlayer.pause()
            mYouTubePlayerView.exitFullScreen()
        }
        mBinding.fab.setImageResource(R.drawable.ic_baseline_play_arrow_white)
        mBinding.fab.contentDescription = Constants.MEDIA_TYPE_VIDEO
        mBinding.fab.show()
        lastMediaType = VIDEO // set up last media type to restore while returning back from full screen mode
    }

    private fun setupImageFab() {
        mBinding.fab.setImageResource(R.drawable.ic_baseline_zoom_in)
        mBinding.fab.contentDescription = Constants.MEDIA_TYPE_IMAGE
        mBinding.fab.show()
        lastMediaType = IMAGE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun getViewContext(): Context {
        return applicationContext
    }

    override fun showAnimation() {

        mBinding.loadingAnim.visibility = View.VISIBLE
    }

    override fun hideAnimation() {
        mBinding.loadingAnim.visibility = View.GONE

    }

    private fun setBackgroundImage(url: String) {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 80f
        circularProgressDrawable.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        circularProgressDrawable.start()


        Glide.with(this)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(getViewContext(), "Unable to load Image", Toast.LENGTH_SHORT)
                        .show()
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    mBinding.backgroundImage.setImageDrawable(resource!!)
                    mBinding.backgroundImage.alpha = BACKGROUND_ALPHA
                    animateFadeOut(mBinding.backgroundImage)
                    return true
                }

            })
            .placeholder(circularProgressDrawable)
            .fitCenter()

            .into(mBinding.backgroundImage)

    }
    /* called after the data loaded to views*/
    override fun postDataUpdate() {
        mBinding.backgroundImage.visibility = View.VISIBLE
        mBinding.cardToolbar.visibility = View.VISIBLE
        mBinding.cardToolbar.startAnimation(animShowFromTop)
        mBinding.youtubePlayerView.visibility = View.GONE
        mBinding.tvDescription.visibility = View.VISIBLE
        mBinding.tvDescription.startAnimation(animShowFromBottom)
    }

    override fun resetViews() {
        mBinding.backgroundImage.clearAnimation()
        mBinding.backgroundImage.visibility = View.GONE
        mBinding.cardToolbar.visibility = View.GONE
        mBinding.youtubePlayerView.visibility = View.GONE
        mBinding.tvDescription.visibility = View.GONE
        mBinding.fab.hide()
    }

    override fun showNetworkError() {
        resetViews()
        mBinding.networkStatus.visibility = View.VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        mBinding.btDatePicker.setOnClickListener {
            setupDatePicker()
        }
        mBinding.fab.setOnClickListener {
            showMediaContent()
        }
        mYouTubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                setFullScreen(true)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


            }

            override fun onYouTubePlayerExitFullScreen() {
                setFullScreen(false)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            }

        })

        mBinding.btRetry.setOnClickListener {
            val connectivityUtil = ConnectivityUtil(this)
            if (connectivityUtil.isConnectingToInternet()) {
                mPresenter.lodUi(this)
                mBinding.networkStatus.visibility = View.GONE
            } else {
                showNetworkError()
            }
        }
        mBinding.backgroundImage.setOnClickListener {
            if (isDatePickerOpen) {
                mBinding.datePicker.visibility = View.GONE
                mBinding.btDatePicker.setImageResource(R.drawable.ic_round_today_24)
                isDatePickerOpen = false
            }

        }
        mBinding.backgroundImage.setOnTouchListener(this)

    }

    //Setting up media content
    private fun showMediaContent() {
        if (mBinding.fab.contentDescription == Constants.MEDIA_TYPE_IMAGE) {
            setFullScreen(true)
            fullScreenHelper.toggleHideyBar()
            animateFadeOut(mBinding.backgroundImage)
            mBinding.backgroundImage.alpha = 1F
            mBinding.cardToolbar.startAnimation(animHideToTop)
            mBinding.cardToolbar.visibility = View.GONE
            mBinding.tvDescription.startAnimation(animHideToBottom)
            mBinding.tvDescription.visibility = View.GONE
            setupButton(CANCEL)
        } else if (mBinding.fab.contentDescription == Constants.MEDIA_TYPE_VIDEO) {
            mBinding.backgroundImage.visibility = View.GONE
            mBinding.cardToolbar.startAnimation(animHideToTop)
            mBinding.cardToolbar.visibility = View.GONE
            mBinding.tvDescription.startAnimation(animHideToBottom)
            mBinding.tvDescription.visibility = View.GONE
            setVideo(mPresenter.videoUrl)
            setupButton(CANCEL)
        } else if (mBinding.fab.contentDescription == Constants.BUTTON_TYPE_CANCEL_STR) {
            fullScreenHelper.toggleHideyBar()
            postDataUpdate()
            mBinding.backgroundImage.visibility = View.VISIBLE
            animateFadeIn(mBinding.backgroundImage)
            mBinding.backgroundImage.alpha = BACKGROUND_ALPHA
            setupButton(lastMediaType)
            mBinding.backgroundImage.setScaleX(1F)
            mBinding.backgroundImage.setScaleY(1F)
            setFullScreen(false)
        }
    }

    private fun setupDatePicker() {
        var currentDate = ""
        val today = Calendar.getInstance()
        val curYear = today.get(Calendar.YEAR)
        val curMonth = today.get(Calendar.MONTH)
        val curDay = today.get(Calendar.DAY_OF_MONTH)
        mBinding.datePicker.fitsSystemWindows = false
        mBinding.datePicker.visibility = when (mBinding.datePicker.visibility) {
            View.VISIBLE -> View.GONE
            View.GONE -> View.VISIBLE
            else -> View.VISIBLE
        }
        if (mBinding.datePicker.visibility == View.VISIBLE) {
            isDatePickerOpen = true
            currentDate = "$curYear-$curMonth-$curDay"
            selectedDate = currentDate
            mBinding.btDatePicker.setImageResource(R.drawable.ic_round_done)
            val day = when(selecedDay){
                0 ->curDay
                else -> selecedDay
            }
            val month = when(selecedMonth){
                0 ->curMonth
                else -> selecedMonth
            }
            val year = when(selecedYear){
                0 ->curYear
                else -> selecedYear
            }
            mBinding.datePicker.init(
                year,
                month,
                day
            ) { view, year, month, day ->
                val monthof = month + 1
                selectedDate = "$year-$monthof-$day"
                selecedDay = day
                selecedMonth = month
                selecedYear = year

            }
        } else {
            isDatePickerOpen = false
            if (!isValidDate()) {
                showMessage("Please select a date before current date!")
                mBinding.btDatePicker.setImageResource(R.drawable.ic_round_today_24)
                mBinding.backgroundImage.visibility = View.GONE
            } else if (selectedDate != currentDate) {
                mPresenter.onDateSelected(selectedDate)
                mBinding.btDatePicker.setImageResource(R.drawable.ic_round_today_24)
                mBinding.backgroundImage.visibility = View.GONE

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun setFullScreen(isFullScreen: Boolean) {
        isFullScreenOn = isFullScreen

    }

    private fun initViews() {
        lifecycle.addObserver(mYouTubePlayerView)
    }

    private fun isValidDate(): Boolean {
        val today = Calendar.getInstance()
        val curYear = today.get(Calendar.YEAR)
        val curMonth = today.get(Calendar.MONTH) + 1
        val curDay = today.get(Calendar.DAY_OF_MONTH)
        var isValid = false
        if ((selecedYear < curYear)) {
            isValid = true
        } else if (selecedYear == curYear) {
            isValid = if (selecedMonth <= curMonth) {
                selecedDay <= curDay
            } else
                false
        }
        return isValid
    }
    private fun initAnimation() {
        animShowFromTop = AnimationUtils.loadAnimation(this, R.anim.anim_show_from_top)
        animHideToTop = AnimationUtils.loadAnimation(this, R.anim.anim_hide_to_top)
        animHideToBottom = AnimationUtils.loadAnimation(this, R.anim.anim_hide_from_bottom)
        animShowFromBottom = AnimationUtils.loadAnimation(this, R.anim.anim_show_from_bottom)
    }

    private fun animateFadeIn(view: View) {
        val fadeIn = AlphaAnimation(1F, BACKGROUND_ALPHA)
        fadeIn.duration = (500)
        fadeIn.fillAfter = true
        view.startAnimation(fadeIn)


    }

    private fun animateFadeOut(view: View) {
        val fadeOut = AlphaAnimation(BACKGROUND_ALPHA, 1F)
        fadeOut.duration = (500)
        fadeOut.fillAfter = true
        view.startAnimation(fadeOut)


    }


    override fun onBackPressed() {
        if(isFullScreenOn){
            showMediaContent()
        }
        else{
            finish()
        }
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        mScaleGestureDetector!!.onTouchEvent(event)
        if(!isFullScreenOn){
            mBinding.backgroundImage.performClick()
        }
        return true
    }
    inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            if (isFullScreenOn) {
                mScaleFactor *= scaleGestureDetector.scaleFactor
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))
                mBinding.backgroundImage.setScaleX(mScaleFactor)
                mBinding.backgroundImage.setScaleY(mScaleFactor)
            }
            else{

            }
            return true
        }

    }

}
