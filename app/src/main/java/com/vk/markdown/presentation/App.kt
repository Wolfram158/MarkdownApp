package com.vk.markdown.presentation

import android.app.Application
import com.vk.markdown.builder.Builder
import com.vk.markdown.di.AppComponent

class App : Application() {
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var builder: Builder

    fun setViewModelFactory(viewModelFactory: ViewModelFactory) {
        this.viewModelFactory = viewModelFactory
    }

    fun setBuilder(builder: Builder) {
        this.builder = builder
    }

    fun getViewModelFactory() = viewModelFactory

    fun getBuilder() = builder

    override fun onCreate() {
        super.onCreate()
        AppComponent.inject(this)
    }
}