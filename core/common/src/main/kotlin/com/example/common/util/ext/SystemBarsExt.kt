package com.example.common.util.ext

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import com.example.common.R

/**
 * SystemBarsExtensions provides extension functions for handling system bars (status bar and navigation bar)
 * in Android activities. These extensions allow for dynamic updates of system bars colors and appearance
 * based on activity lifecycle.
 */

/**
 * Adds a lifecycle observer to update the status bar color and appearance.
 * The changes will be applied when the activity enters the STARTED state and
 * will be reset when the activity enters the STOPPED state.
 *
 * @param statusBarColorRes The color resource ID for the status bar
 * @param isLightStatusBar Whether to use light status bar icons (true for light background, false for dark)
 */
fun ComponentActivity.addStatusBarColorUpdate(
    @ColorRes statusBarColorRes: Int,
    isLightStatusBar: Boolean = true
) {
    lifecycle.addObserver(
        StatusBarColorLifecycleObserver(
            activity = this,
            statusBarColor = getColor(statusBarColorRes),
            isLightStatusBar = isLightStatusBar
        )
    )
}

/**
 * Adds a lifecycle observer to update the navigation bar color and appearance.
 * The navigation bar icons' color will be automatically adjusted based on the
 * background color brightness.
 *
 * @param navigationBarColorRes The color resource ID for the navigation bar
 */
fun ComponentActivity.addNavigationBarColorUpdate(
    @ColorRes navigationBarColorRes: Int
) {
    lifecycle.addObserver(
        NavigationBarColorLifecycleObserver(
            activity = this,
            navigationBarColor = getColor(navigationBarColorRes)
        )
    )
}

/**
 * Adds a lifecycle observer to update both status bar and navigation bar colors
 * and appearance simultaneously.
 *
 * @param systemBarsColorRes The color resource ID for both status and navigation bars
 * @param isLightStatusBar Whether to use light status bar icons (true for light background, false for dark)
 */
fun ComponentActivity.addSystemBarsColorUpdate(
    @ColorRes systemBarsColorRes: Int,
    isLightStatusBar: Boolean = true
) {
    val color = getColor(systemBarsColorRes)
    lifecycle.addObserver(
        SystemBarsColorLifecycleObserver(
            activity = this,
            statusBarColor = color,
            navigationBarColor = color,
            isLightStatusBar = isLightStatusBar
        )
    )
}

/**
 * Adds a lifecycle observer to update the status bar color and appearance in a Fragment.
 * The changes will be applied when the Fragment enters the STARTED state and
 * will be reset when the Fragment enters the STOPPED state.
 *
 * Note: This will affect the entire activity's status bar, not just the Fragment's portion.
 *
 * @param statusBarColorRes The color resource ID for the status bar
 * @param isLightStatusBar Whether to use light status bar icons (true for light background, false for dark)
 */
fun Fragment.addStatusBarColorUpdate(
    @ColorRes statusBarColorRes: Int,
    isLightStatusBar: Boolean = true
) {
    requireActivity().let { activity ->
        viewLifecycleOwner.lifecycle.addObserver(
            StatusBarColorLifecycleObserver(
                activity = activity,
                statusBarColor = activity.getColor(statusBarColorRes),
                isLightStatusBar = isLightStatusBar
            )
        )
    }
}

/**
 * Adds a lifecycle observer to update the navigation bar color and appearance in a Fragment.
 * The navigation bar icons' color will be automatically adjusted based on the
 * background color brightness.
 *
 * Note: This will affect the entire activity's navigation bar, not just the Fragment's portion.
 *
 * @param navigationBarColorRes The color resource ID for the navigation bar
 */
fun Fragment.addNavigationBarColorUpdate(
    @ColorRes navigationBarColorRes: Int
) {
    requireActivity().let { activity ->
        viewLifecycleOwner.lifecycle.addObserver(
            NavigationBarColorLifecycleObserver(
                activity = activity,
                navigationBarColor = activity.getColor(navigationBarColorRes)
            )
        )
    }
}

/**
 * Adds a lifecycle observer to update both status bar and navigation bar colors
 * and appearance simultaneously in a Fragment.
 *
 * Note: This will affect the entire activity's system bars, not just the Fragment's portion.
 *
 * @param systemBarsColorRes The color resource ID for both status and navigation bars
 * @param isLightStatusBar Whether to use light status bar icons (true for light background, false for dark)
 */
fun Fragment.addSystemBarsColorUpdate(
    @ColorRes systemBarsColorRes: Int,
    isLightStatusBar: Boolean = true
) {
    requireActivity().let { activity ->
        val color = activity.getColor(systemBarsColorRes)
        viewLifecycleOwner.lifecycle.addObserver(
            SystemBarsColorLifecycleObserver(
                activity = activity,
                statusBarColor = color,
                navigationBarColor = color,
                isLightStatusBar = isLightStatusBar
            )
        )
    }
}

/**
 * Lifecycle observer for handling status bar color and appearance changes.
 */
@Suppress("DEPRECATION")
private class StatusBarColorLifecycleObserver(
    activity: Activity,
    @ColorInt private val statusBarColor: Int,
    private val isLightStatusBar: Boolean
) : DefaultLifecycleObserver {
    private val defaultStatusBarColor = activity.getColor(R.color.white)
    private val activityRef = WeakReference(activity)

    override fun onStart(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            updateStatusBar(window)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightStatusBar) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            val uiVisibilityFlags = if (isLightStatusBar) {
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
            window.decorView.systemUiVisibility = uiVisibilityFlags
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
}

/**
 * Lifecycle observer for handling navigation bar color and appearance changes.
 */
@Suppress("DEPRECATION")
private class NavigationBarColorLifecycleObserver(
    activity: Activity,
    @ColorInt private val navigationBarColor: Int
) : DefaultLifecycleObserver {
    private val defaultNavigationBarColor = activity.getColor(R.color.white)
    private val activityRef = WeakReference(activity)

    override fun onStart(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            updateNavigationBar(window)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            resetNavigationBar(window)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activityRef.clear()
    }

    private fun updateNavigationBar(window: Window) {
        window.navigationBarColor = navigationBarColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val isLightNavigation = ColorUtils.calculateLuminance(navigationBarColor) > 0.5
            window.insetsController?.setSystemBarsAppearance(
                if (isLightNavigation) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            val currentFlags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = currentFlags or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    private fun resetNavigationBar(window: Window) {
        window.navigationBarColor = defaultNavigationBarColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            window.decorView.systemUiVisibility = 0
        }
    }
}

/**
 * Lifecycle observer for handling both status bar and navigation bar color and appearance changes.
 */
@Suppress("DEPRECATION")
private class SystemBarsColorLifecycleObserver(
    activity: Activity,
    @ColorInt private val statusBarColor: Int,
    @ColorInt private val navigationBarColor: Int,
    private val isLightStatusBar: Boolean
) : DefaultLifecycleObserver {
    private val defaultSystemBarColor = activity.getColor(R.color.white)
    private val activityRef = WeakReference(activity)

    override fun onStart(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            updateSystemBars(window)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        activityRef.get()?.window?.let { window ->
            resetSystemBars(window)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activityRef.clear()
    }

    private fun updateSystemBars(window: Window) {
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val isLightNavigation = ColorUtils.calculateLuminance(navigationBarColor) > 0.5

            var appearanceFlags = if (isLightStatusBar) {
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            } else 0

            appearanceFlags = appearanceFlags or if (isLightNavigation) {
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            } else 0

            window.insetsController?.setSystemBarsAppearance(
                appearanceFlags,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            var uiVisibilityFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            if (isLightStatusBar) {
                uiVisibilityFlags = uiVisibilityFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            window.decorView.systemUiVisibility = uiVisibilityFlags
        }
    }

    private fun resetSystemBars(window: Window) {
        window.statusBarColor = defaultSystemBarColor
        window.navigationBarColor = defaultSystemBarColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            window.decorView.systemUiVisibility = 0
        }
    }
}