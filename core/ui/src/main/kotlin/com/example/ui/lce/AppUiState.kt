package com.example.ui.lce

sealed class UiState<out T> {

    val isLoading: Boolean get() = this is Loading
    val isSuccessful: Boolean get() = this is Success<*>
    val isNoContent: Boolean get() = this is NoContent
    val isError: Boolean get() = this is Error

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data]"
        is NoContent -> "NoContent[reason=$reason]"
        is Error -> "Error[message=${error?.localizedMessage ?: "unknown error"}]"
        is Loading -> "Loading"
    }

    fun successOrNull(): T? = (this as? Success<T>)?.data
}

data class Success<out T>(val data: T) : UiState<T>()
data class NoContent(val reason: String) : UiState<Nothing>()
data class Error(val error: Throwable?) : UiState<Nothing>()
object Loading : UiState<Nothing>()
