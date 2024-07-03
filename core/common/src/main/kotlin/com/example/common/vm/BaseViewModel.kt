package com.example.common.vm

import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.LogUtils
import com.hjq.toast.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

open class BaseViewModel : ViewModel() {

    protected fun <T> fetchData(fetch: suspend () -> T): Flow<T> = flow {
        emit(fetch())
    }.catch { e ->
        handleError(e)
        throw Throwable(e)
    }.flowOn(Dispatchers.IO)

    private fun handleError(e: Throwable) {
        when (e) {
            is ConnectException -> {
                Toaster.show("Unable to connect to the server, please check your network connection.")
            }
            is TimeoutException -> {
                Toaster.show("Request timed out, please try again.")
            }
            is SocketTimeoutException -> {
                Toaster.show("Connection timed out, please try again.")
            }
            is UnknownHostException -> {
                Toaster.show("Unable to resolve host, please check your network connection.")
            }
            is IOException -> {
                Toaster.show("Network error, please check your connection.")
            }
            else -> {
                LogUtils.e("Network data request exception: $e")
            }
        }
    }

}
