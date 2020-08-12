package com.developer.chithlal.nasaphotooftheday.dependancy_injection

import com.developer.chithlal.nasaphotooftheday.main_ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,MainActivityModule::class])
interface AppComponent {
    fun inject(target:MainActivity)
}