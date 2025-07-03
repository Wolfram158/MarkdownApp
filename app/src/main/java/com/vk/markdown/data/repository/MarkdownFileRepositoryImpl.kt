package com.vk.markdown.data.repository

import com.vk.markdown.domain.repository.MarkdownFileRepository
import com.vk.markdown.net.NetDownloader

class MarkdownFileRepositoryImpl(
    private val downloader: NetDownloader
) : MarkdownFileRepository {
    override fun downloadImage(link: String): ByteArray? {
        return downloader.download(link, useCache = true)
    }

    override fun loadFile(link: String): String {
        return downloader.download(link, useCache = false)?.toString(Charsets.UTF_8) ?: ""
    }
}