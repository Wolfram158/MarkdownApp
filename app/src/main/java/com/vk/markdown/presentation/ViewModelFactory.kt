package com.vk.markdown.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vk.markdown.domain.usecase.DownloadImageUseCase

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val downloadImageUseCase: DownloadImageUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarkdownFileViewModel::class.java)) {
            return MarkdownFileViewModel(downloadImageUseCase) as T
        }
        throw RuntimeException("Unidentified view model")
    }
}