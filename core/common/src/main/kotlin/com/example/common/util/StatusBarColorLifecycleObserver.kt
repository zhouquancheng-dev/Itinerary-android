package com.example.common.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.common.R
import java.lang.ref.WeakReference

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No activity found")
}

@Suppress("DEPRECATION")
class StatusBarColorLifecycleObserver(
    activity: Activity,
    @ColorInt private val statusBarColor: Int,
) : DefaultLifecycleObserver {
    private val defaultStatusBarColor = activity.getColor(R.color.white)
    private val activityRef = WeakReference(activity)

    override fun onStart(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            updateStatusBar(window)
            updateNavigationBar(window)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            resetStatusBar(window)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activityRef.clear()
    }

    private fun updateStatusBar(window: Window) {
        window.statusBarColor = statusBarColor
        val isLightStatusBar = isLightStatusBar(statusBarColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightStatusBar) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.decorView.systemUiVisibility =
                if (isLightStatusBar) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
        }
    }

    private fun updateNavigationBar(window: Window) {
        window.navigationBarColor = statusBarColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (ColorUtils.calculateLuminance(statusBarColor) > 0.5) {
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                } else 0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    private fun resetStatusBar(window: Window) {
        window.statusBarColor = defaultStatusBarColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.decorView.systemUiVisibility = 0
        }
    }

    private fun isLightStatusBar(@ColorInt color: Int): Boolean {
        val activity = activityRef.get()
        return if (activity != null && android.graphics.Color.alpha(color) == 0) {
            (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO)
        } else {
            ColorUtils.calculateLuminance(color) > 0.5
        }
    }
}
