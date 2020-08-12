package com.developer.chithlal.nasaphotooftheday.main_ui

import android.content.res.Resources
import com.developer.chithlal.nasaphotooftheday.R
import com.developer.chithlal.nasaphotooftheday.main_ui.MainContract.*
import com.developer.chithlal.nasaphotooftheday.network.PodApiClient
import com.developer.chithlal.nasaphotooftheday.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.annotation.Resource

class MainModel : Model {
    lateinit var mPresenter:MainContract.Presenter
    override fun getPod(date: String?) {
        val api_key: String = Constants.API_KEY


        val call = when(date){
            null -> PodApiClient.getClient.getPicOfToday(api_key)
            else -> PodApiClient.getClient.getPicOfDate(api_key,date)
        }
        call.enqueue(object : Callback<NasaPod> {
            override fun onFailure(call: Call<NasaPod>, t: Throwable) {

                mPresenter.onRequestFailed("Sorry...Unable to load Pic of the day!\n"+t.localizedMessage)
            }

            override fun onResponse(call: Call<NasaPod>, response: Response<NasaPod>) {
                if (response.isSuccessful)
                    mPresenter.onDataLoaded(response.body()!!)
                else
                    mPresenter.onRequestFailed("Sorry...Unable to load Pic of the day!")

            }
        })
    }

    override fun setPresenter(presenter: Presenter) {
        mPresenter = presenter
    }

}