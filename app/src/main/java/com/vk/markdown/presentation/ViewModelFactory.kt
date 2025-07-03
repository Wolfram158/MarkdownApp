package com.vk.markdown.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import com.vk.markdown.domain.usecase.LoadFileUseCase

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val downloadImageUseCase: DownloadImageUseCase,
    private val loadFileUseCase: LoadFileUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarkdownFileViewModel::class.java)) {
            return MarkdownFileViewModel(downloadImageUseCase, loadFileUseCase) as T
        }
        throw RuntimeException("Unidentified ViewModel")
    }
}