package com.example.ui.components

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.hjq.toast.Toaster
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 返回拦截
 */
@Composable
fun ExitIntercept() {
    val context = LocalContext.current
    var isBack by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler(isBack) {
        if (isBack) {
            (context as Activity).finish()
        } else {
            isBack = true
            scope.launch {
                delay(2000)
                isBack = false
            }
            Toaster.show("再按一次退出")
        }
    }
}

