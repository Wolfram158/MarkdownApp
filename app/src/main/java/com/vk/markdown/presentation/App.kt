package com.vk.markdown.presentation

import android.app.Application
import com.vk.markdown.builder.MarkdownViewBuilder
import com.vk.markdown.di.AppComponent

class App : Application() {
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var markdownViewBuilder: MarkdownViewBuilder

    fun setViewModelFactory(viewModelFactory: ViewModelFactory) {
        this.viewModelFactory = viewModelFactory
    }

    fun setBuilder(markdownViewBuilder: MarkdownViewBuilder) {
        this.markdownViewBuilder = markdownViewBuilder
    }

    fun getViewModelFactory() = viewModelFactory

    fun getBuilder() = markdownViewBuilder

    override fun onCreate() {
        super.onCreate()
        AppComponent.inject(this)
    }
}