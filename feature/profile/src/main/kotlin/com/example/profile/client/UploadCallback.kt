package com.example.profile.client

interface UploadCallback {
    fun onStart()
    fun onSuccess(objectUrl: String)
    fun onProgress(currentSize: Long, totalSize: Long)
    fun onFailure(exception: Exception)
}
