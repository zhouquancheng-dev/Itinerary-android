package com.example.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.common.util.StatusBarColorLifecycleObserver
import com.example.common.util.findActivity

@Composable
fun StatusBarColorUpdateEffect(color: Color) {
    if (LocalInspectionMode.current) return // 在预览模式下无需操作

    val activity = LocalContext.current.findActivity() // 获取当前 Activity
    val lifecycleOwner = LocalLifecycleOwner.current // 获取当前 LifecycleOwner

    if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
        return
    }

    val observer = remember { StatusBarColorLifecycleObserver(activity, color.toArgb()) }

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}