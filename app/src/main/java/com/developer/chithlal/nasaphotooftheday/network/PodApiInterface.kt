package com.developer.chithlal.nasaphotooftheday.network


import com.developer.chithlal.nasaphotooftheday.main_ui.NasaPod
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PodApiInterface {

    @GET("apod")
    fun getPicOfToday(@Query("api_key") apiKey:String): Call<NasaPod>
    @GET("apod?")
    fun getPicOfDate( @Query("api_key") apiKey: String,@Query("date") date:String) : Call<NasaPod>
}