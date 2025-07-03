package com.vk.markdown.domain.usecase

import com.vk.markdown.domain.repository.MarkdownFileRepository

class LoadFileUseCase(
    private val repository: MarkdownFileRepository
) {
    operator fun invoke(link: String) = repository.loadFile(link)
}