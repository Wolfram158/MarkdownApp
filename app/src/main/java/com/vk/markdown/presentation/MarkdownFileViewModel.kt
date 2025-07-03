package com.vk.markdown.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import com.vk.markdown.domain.usecase.LoadFileUseCase
import kotlin.concurrent.thread

class MarkdownFileViewModel(
    private val downloadImageUseCase: DownloadImageUseCase,
    private val loadFileUseCase: LoadFileUseCase
) : ViewModel() {
    private val _signal: MutableLiveData<Result> = MutableLiveData(Result.Initial())
    val signal: LiveData<Result>
        get() = _signal

    fun downloadImage(link: String, callback: (ByteArray?) -> Unit) {
        thread {
            try {
                callback(downloadImageUseCase(link))
                _signal.postValue(Result.Success())
            } catch (_: Exception) {
                _signal.postValue(Result.Error())
            }
        }
    }

    fun loadFile(link: String, callback: (String) -> Unit) {
        thread {
            try {
                callback(loadFileUseCase(link))
                _signal.postValue(Result.Success())
            } catch (_: Exception) {
                _signal.postValue(Result.Error())
            }
        }
    }

    sealed class Result {
        class Initial : Result()
        class Success : Result()
        class Error : Result()
    }
}