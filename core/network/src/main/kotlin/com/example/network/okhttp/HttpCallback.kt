package com.example.network.okhttp

interface HttpCallback<T> {
    fun onSuccess(data: T)
    fun onError(error: String)
}