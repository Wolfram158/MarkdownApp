package com.vk.markdown.domain.usecase

import com.vk.markdown.domain.repository.MarkdownFileRepository

class DownloadImageUseCase(
    private val repository: MarkdownFileRepository
) {
    operator fun invoke(link: String) = repository.downloadImage(link)
}