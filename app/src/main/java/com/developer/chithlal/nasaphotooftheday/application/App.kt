package com.developer.chithlal.nasaphotooftheday.application

import android.app.Application
import com.developer.chithlal.nasaphotooftheday.dependancy_injection.AppComponent
import com.developer.chithlal.nasaphotooftheday.dependancy_injection.AppModule
import com.developer.chithlal.nasaphotooftheday.dependancy_injection.DaggerAppComponent

class App : Application() {
    lateinit var appComponent : AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }
    private fun initDagger(app: App):AppComponent =
         DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()


}