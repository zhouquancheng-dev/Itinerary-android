package com.example.common.base.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.di.AppDispatchers.IO
import com.example.common.di.Dispatcher
import com.hjq.toast.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    companion object {
        private val TAG = BaseViewModel::class.java.simpleName
    }

    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                block()
            }.onFailure { e ->
                Log.e(TAG, "launch onFailure: ", e)
            }
        }
    }

    protected fun <T> fetchData(block: suspend () -> T): Flow<T> = flow {
        emit(block())
    }.catch { e ->
        handleError(e)
        throw Throwable(e)
    }.flowOn(ioDispatcher)

    private fun handleError(e: Throwable) {
        when (e) {
            is ConnectException -> {
                Toaster.show("Unable to connect to the server, please check your network connection.")
            }
            is TimeoutException -> {
                Toaster.show("Request timed out, please try again.")
            }
            is UnknownHostException -> {
                Toaster.show("Unable to resolve host, please check your network connection.")
            }
            is IOException -> {
                Toaster.show("Network error, please check your connection.")
            }
            else -> {
                Log.e(TAG, "Network data request exception: $e")
            }
        }
    }

}
