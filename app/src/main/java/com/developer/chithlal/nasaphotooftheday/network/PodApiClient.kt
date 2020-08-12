package com.developer.chithlal.nasaphotooftheday.network

import android.content.Context
import android.content.res.Resources
import com.developer.chithlal.nasaphotooftheday.R
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PodApiClient {
    var baseUrl : String = "https://api.nasa.gov/planetary/"
    val getClient:PodApiInterface
    get()  {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(PodApiInterface::class.java)
    }
}