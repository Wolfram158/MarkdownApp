package com.vk.markdown.domain.repository

interface MarkdownFileRepository {
    fun downloadImage(link: String): ByteArray?

    fun loadFile(link: String): String
}