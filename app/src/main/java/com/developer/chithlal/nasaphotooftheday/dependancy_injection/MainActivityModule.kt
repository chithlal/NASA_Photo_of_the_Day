package com.developer.chithlal.nasaphotooftheday.dependancy_injection

import com.developer.chithlal.nasaphotooftheday.main_ui.MainContract
import com.developer.chithlal.nasaphotooftheday.main_ui.MainModel
import com.developer.chithlal.nasaphotooftheday.main_ui.MainPresenter
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun providePresenter(model:MainContract.Model):MainContract.Presenter = MainPresenter(model)

    @Provides
    fun provideModel():MainContract.Model = MainModel()

}