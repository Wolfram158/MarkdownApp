package com.vk.markdown.di

import com.vk.cache.LruCache
import com.vk.markdown.builder.MarkdownViewBuilder
import com.vk.markdown.data.repository.MarkdownFileRepositoryImpl
import com.vk.markdown.domain.repository.MarkdownFileRepository
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import com.vk.markdown.domain.usecase.LoadFileUseCase
import com.vk.markdown.presentation.App
import com.vk.markdown.presentation.ViewModelFactory
import com.vk.net.NetDownloader

object AppComponent {
    private const val CACHE_CAPACITY = 8

    fun inject(app: App) {
        val cache = LruCache<String, ByteArray>(CACHE_CAPACITY)
        val downloader = NetDownloader(cache)
        val markdownFileRepository: MarkdownFileRepository = MarkdownFileRepositoryImpl(downloader)
        val downloadImageUseCase = DownloadImageUseCase(markdownFileRepository)
        val loadFileUseCase = LoadFileUseCase(markdownFileRepository)
        app.setViewModelFactory(ViewModelFactory(downloadImageUseCase, loadFileUseCase))
        MarkdownViewBuilder().apply {
            setDownloadImageUseCase(downloadImageUseCase)
            app.setBuilder(this)
        }
    }
}