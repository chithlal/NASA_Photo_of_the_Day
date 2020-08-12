package com.developer.chithlal.nasaphotooftheday.main_ui

import android.content.Context
import com.developer.chithlal.nasaphotooftheday.util.Constants

interface MainContract {
    interface View{

        fun setTitle(title : String)
        fun setDescription(description : String)
        fun setImage(imageUrl : String)
        fun setVideo(videoUrl : String)
        fun setupButton(type : Constants.BUTTON_TYPE)
        fun showMessage(message : String)
        fun getViewContext():Context
        fun showAnimation()
        fun hideAnimation()
        fun postDataUpdate()
        fun resetViews()
        fun showNetworkError()
    }
    interface Presenter{
        var videoUrl:String
        var imageUrl:String
        fun lodUi(view : MainContract.View)
        fun onDateSelected(date : String)
        fun onDataLoaded(data: NasaPod)
        fun onRequestFailed(message: String)
        fun passMessage(message: String)

    }
    interface Model{
        fun getPod(date:String?)
        fun setPresenter(presenter: MainContract.Presenter)
    }


}