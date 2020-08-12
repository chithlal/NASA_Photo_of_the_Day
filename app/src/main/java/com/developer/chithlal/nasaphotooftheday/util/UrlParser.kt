package com.developer.chithlal.nasaphotooftheday.util

object UrlParser {
//"https://www.youtube.com/embed/Ilifg26TZrI?rel=0" sample url
    fun getVideoId(url:String?):String{
    return if (!url.isNullOrBlank()) {
        val urlArray = url.split("/")

        val finalPortion = urlArray[urlArray.size - 1]
        val idString = finalPortion.split('?')
        idString[0]
    }
    else "Ilifg26TZrI"
    }

}