package com.vk.markdown.di

import com.vk.markdown.cache.LRUCache
import com.vk.markdown.data.repository.MarkdownFileRepositoryImpl
import com.vk.markdown.domain.repository.MarkdownFileRepository
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import com.vk.markdown.net.NetDownloader
import com.vk.markdown.presentation.App

object AppComponent {
    private const val CACHE_CAPACITY = 16

    fun inject(app: App) {
        val cache = LRUCache<String, ByteArray>(CACHE_CAPACITY)
        val downloader = NetDownloader(cache)
        val markdownFileRepository: MarkdownFileRepository = MarkdownFileRepositoryImpl(downloader)
        app.setDownloadImageUseCase(DownloadImageUseCase(markdownFileRepository))
    }
}