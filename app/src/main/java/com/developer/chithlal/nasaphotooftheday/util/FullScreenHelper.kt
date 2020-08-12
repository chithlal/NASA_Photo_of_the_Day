package com.developer.chithlal.nasaphotooftheday.util

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import android.view.WindowManager


class FullScreenHelper(private val context: Activity, vararg views: View) {
    private val views: Array<View> = views as Array<View>



    private fun hideSystemUi(mDecorView: View) {
        mDecorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun showSystemUi(mDecorView: View) {
        mDecorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    fun toggleHideyBar() {


        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.


        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        val uiOptions: Int = context.getWindow().getDecorView().getSystemUiVisibility()

        var newUiOptions = uiOptions

        val isImmersiveModeEnabled =
            uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ")
        } else {
            Log.i(TAG, "Turning immersive mode mode on.")
        }


        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        context.getWindow().getDecorView().setSystemUiVisibility(newUiOptions)


    }

}