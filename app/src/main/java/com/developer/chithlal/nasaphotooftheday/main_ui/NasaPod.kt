package com.developer.chithlal.nasaphotooftheday.main_ui

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NasaPod (
    @SerializedName("copyright")
    val copyright: String? = null,

    @SerializedName("date")

    val date: String? = null,

    @SerializedName("explanation")
    @Expose
    val explanation: String? = null,

    @SerializedName("media_type")
    @Expose
    val mediaType: String? = null,

    @SerializedName("service_version")
    @Expose
    val serviceVersion: String? = null,

    @SerializedName("title")
    @Expose
    val title: String? = null,

    @SerializedName("url")
    @Expose
    val url: String? = null,

    @SerializedName("hdurl")
    @Expose
    val hdurl: String? = null


)