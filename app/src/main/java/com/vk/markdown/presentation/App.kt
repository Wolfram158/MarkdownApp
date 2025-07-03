package com.vk.markdown.presentation

import android.app.Application
import com.vk.markdown.di.AppComponent

class App : Application() {
    private lateinit var viewModelFactory: ViewModelFactory

    fun setViewModelFactory(viewModelFactory: ViewModelFactory) {
        this.viewModelFactory = viewModelFactory
    }

    fun getViewModelFactory() = viewModelFactory

    override fun onCreate() {
        super.onCreate()
        AppComponent.inject(this)
    }
}