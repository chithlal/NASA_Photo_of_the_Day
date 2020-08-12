package com.developer.chithlal.nasaphotooftheday.main_ui

import com.developer.chithlal.nasaphotooftheday.util.ConnectivityUtil
import com.developer.chithlal.nasaphotooftheday.util.Constants
import com.developer.chithlal.nasaphotooftheday.util.UrlParser
import java.util.*
import java.util.logging.Handler
import kotlin.concurrent.schedule
import kotlin.concurrent.timer
import kotlinx.coroutines.*
class MainPresenter(val model:MainContract.Model):MainContract.Presenter {
    lateinit var mView: MainContract.View
    override var videoUrl: String = ""
        get() = field
        set(value) {}
    override var imageUrl: String = ""
        get() = field
        set(value) {}
    lateinit var connectivityUtil:ConnectivityUtil

    override fun lodUi(view: MainContract.View) {
        mView = view

        model.setPresenter(this)
        connectivityUtil = ConnectivityUtil(mView.getViewContext())
        if (connectivityUtil.isConnectingToInternet()) {
            mView.showAnimation()
            model.getPod(null)
        }
        else{
            mView.showNetworkError()
        }
    }

    override fun onDateSelected(date: String) {
        if (connectivityUtil.isConnectingToInternet()) {
            mView.resetViews()
            mView.showAnimation()
            model.getPod(date)
        }
        else{
            mView.showNetworkError()
        }
    }

    override fun onDataLoaded(data: NasaPod) {
        if (data.mediaType == Constants.MEDIA_TYPE_IMAGE) {
            mView.setImage(data.hdurl!!)
            imageUrl = data.hdurl
            mView.setupButton(Constants.BUTTON_TYPE.IMAGE)
        }
        else {
            mView.setupButton(Constants.BUTTON_TYPE.VIDEO)
            videoUrl = data.url!!
            val vidId = UrlParser.getVideoId(data.url!!)
            val thumbnailUrl =Constants.thumbnailUrl+"$vidId/0.jpg"
            imageUrl = thumbnailUrl;
            mView.setImage(thumbnailUrl)
        }
        mView.setTitle(data.title!!)
        mView.setDescription(data.explanation!!)
        GlobalScope.launch {
            delay(1500L)
        }
        mView.hideAnimation()
        mView.postDataUpdate()
    }

    override fun onRequestFailed(message: String) {
        mView.showMessage(message)
    }

    override fun passMessage(message: String) {
        mView.showMessage(message)
    }


}