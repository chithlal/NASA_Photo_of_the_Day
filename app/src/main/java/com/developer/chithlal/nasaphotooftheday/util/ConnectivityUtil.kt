package com.developer.chithlal.nasaphotooftheday.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

class ConnectivityUtil(private val mContext: Context) {
    fun isConnectingToInternet(): Boolean {
        val connectivity = mContext.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        for (i in info)
            if (i.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        return false
    }
}