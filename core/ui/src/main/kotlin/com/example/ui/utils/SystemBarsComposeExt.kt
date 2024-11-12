package com.example.ui.utils

import android.os.Build
import android.view.Window
import android.view.WindowInsetsController
import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.graphics.ColorUtils
import android.app.Activity
import android.content.res.Configuration
import android.view.View
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.common.util.ext.findActivity
import java.lang.ref.WeakReference

@Composable
fun SystemBarsColorEffect(color: Color) {
    // Skip in preview mode
    if (LocalInspectionMode.current) return

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Skip if lifecycle is destroyed
    if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
        return
    }

    val activity = remember(context) { context.findActivity() }
    val observer = remember(activity, color) {
        StatusBarColorLifecycleObserver(activity, color.toArgb())
    }

    DisposableEffect(lifecycleOwner, observer) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Base observer class for system bars operations with lifecycle awareness.
 */
@Suppress("DEPRECATION")
private abstract class BaseSystemBarsLifecycleObserver(
    activity: Activity
) : DefaultLifecycleObserver {
    val activityRef = WeakReference(activity)

    override fun onStart(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            applyColors(window)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            resetColors(window)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activityRef.clear()
    }

    fun isLightColor(@ColorInt color: Int): Boolean {
        val activity = activityRef.get()
        return if (activity != null && android.graphics.Color.alpha(color) == 0) {
            (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO)
        } else {
            ColorUtils.calculateLuminance(color) > 0.5
        }
    }

    abstract fun applyColors(window: Window)
    abstract fun resetColors(window: Window)
}

@Suppress("DEPRECATION")
private class StatusBarColorLifecycleObserver(
    activity: Activity,
    @ColorInt private val statusBarColor: Int
) : BaseSystemBarsLifecycleObserver(activity) {

    override fun applyColors(window: Window) {
        // Update status bar
        window.statusBarColor = statusBarColor
        val isLightStatusBar = isLightColor(statusBarColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightStatusBar) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            var flags = window.decorView.systemUiVisibility
            flags = if (isLightStatusBar) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            window.decorView.systemUiVisibility = flags
        }

        // Update navigation bar
        window.navigationBarColor = statusBarColor
        val isLightNavBar = isLightColor(statusBarColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightNavBar) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            var flags = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            flags = if (isLightNavBar) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            window.decorView.systemUiVisibility = flags
        }
    }

    override fun resetColors(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }
        } else {
            window.decorView.systemUiVisibility = 0
        }
    }
}