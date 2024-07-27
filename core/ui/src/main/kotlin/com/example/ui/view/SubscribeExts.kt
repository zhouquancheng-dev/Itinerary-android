package com.example.ui.view

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.common.flowbus.FlowBus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Activity 的扩展函数
inline fun <reified T> AppCompatActivity.subscribe(
    sticky: Boolean = false,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    noinline filter: (T) -> Boolean = { true },
    crossinline onEvent: (T) -> Unit
) {
    FlowBus.subscribe(this, sticky, dispatcher, false, filter, onEvent)
}

// Fragment 的扩展函数
inline fun <reified T> Fragment.subscribe(
    sticky: Boolean = false,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    noinline filter: (T) -> Boolean = { true },
    crossinline onEvent: (T) -> Unit
) {
    FlowBus.subscribe(viewLifecycleOwner, sticky, dispatcher, false, filter, onEvent)
}

// Jetpack Compose 的扩展函数
@Composable
inline fun <reified T> subscribe(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    sticky: Boolean = false,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    noinline filter: (T) -> Boolean = { true },
    crossinline onEvent: (T) -> Unit
) {
    DisposableEffect(lifecycleOwner) {
        val job = FlowBus.subscribe(lifecycleOwner, sticky, dispatcher, false, filter, onEvent)
        onDispose {
            job.cancel()
        }
    }
}