package com.vk.markdown.di

import com.vk.markdown.cache.LRUCache
import com.vk.markdown.data.repository.MarkdownFileRepositoryImpl
import com.vk.markdown.domain.repository.MarkdownFileRepository
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import com.vk.markdown.net.NetDownloader
import com.vk.markdown.presentation.App
import com.vk.markdown.presentation.ViewModelFactory

object AppComponent {
    private const val CACHE_CAPACITY = 8

    fun inject(app: App) {
        val cache = LRUCache<String, ByteArray>(CACHE_CAPACITY)
        val downloader = NetDownloader(cache)
        val markdownFileRepository: MarkdownFileRepository = MarkdownFileRepositoryImpl(downloader)
        val downloadImageUseCase = DownloadImageUseCase(markdownFileRepository)
        app.setViewModelFactory(ViewModelFactory(downloadImageUseCase))
    }
}