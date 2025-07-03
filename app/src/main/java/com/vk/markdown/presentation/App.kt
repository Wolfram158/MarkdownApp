package com.vk.markdown.presentation

import android.app.Application
import com.vk.markdown.di.AppComponent
import com.vk.markdown.domain.usecase.DownloadImageUseCase

class App : Application() {
    private lateinit var downloadImageUseCase: DownloadImageUseCase

    fun setDownloadImageUseCase(downloadImageUseCase: DownloadImageUseCase) {
        this.downloadImageUseCase = downloadImageUseCase
    }

    fun getDownloadImageUseCase() = downloadImageUseCase

    override fun onCreate() {
        super.onCreate()
        AppComponent.inject(this)
    }
}