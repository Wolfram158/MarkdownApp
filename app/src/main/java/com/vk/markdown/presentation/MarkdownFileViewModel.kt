package com.vk.markdown.presentation

import androidx.lifecycle.ViewModel
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import kotlin.concurrent.thread

class MarkdownFileViewModel(
    private val downloadImageUseCase: DownloadImageUseCase
) : ViewModel() {
    fun downloadImage(link: String, callback: (ByteArray?) -> Unit) {
        thread {
            val bytes = downloadImageUseCase(link)
            callback(bytes)
        }
    }
}