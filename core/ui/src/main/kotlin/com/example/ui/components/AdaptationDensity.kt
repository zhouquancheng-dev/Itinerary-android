package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * ```
 * 换算公式：
 * px = density * dp
 * density = dpi / 160
 * px = dp * (dpi / 160)
 * px = sp * fontScale * density

 * 当前应用选择采用 3200px * 1440px 分辨率的屏幕适配，计算打印出各项参数如下：
 * densityDpi：560dpi
 * density: 3.5
 * widthPixels：1440
 * heightPixels：3007
 * widthDp约为：411.42856dp
 * heightDp约为：859.1429dp
 *
 * val displayMetrics = getContext.resources.displayMetrics
 * Log.i("TAG", "densityDpi: ${displayMetrics.densityDpi}")
 * Log.i("TAG", "density: ${displayMetrics.density}")
 * Log.i("TAG", "widthPixels: ${displayMetrics.widthPixels}")
 * Log.i("TAG", "heightPixels: ${displayMetrics.heightPixels}")
 * Log.i("TAG", "widthDp: ${displayMetrics.widthPixels / displayMetrics.density}")
 * Log.i("TAG", "heightDp: ${displayMetrics.heightPixels / displayMetrics.density}")
 ```
 * 以屏幕像素高度进行适配
 */
@Composable
fun AdaptationPixelHeight(
    content: @Composable () -> Unit
) {
    val fontScale = LocalDensity.current.fontScale
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val heightPixels = displayMetrics.heightPixels

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = heightPixels / 859f,
            fontScale = fontScale
        )
    ) {
        content()
    }
}

/**
 * 以屏幕像素宽度进行适配
 */
@Composable
fun AdaptationPixelWidth(
    content: @Composable () -> Unit
) {
    val fontScale = LocalDensity.current.fontScale
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = widthPixels / 411.5f,
            fontScale = fontScale
        )
    ) {
        content()
    }
}

/**
 * @param baseWidthDp 默认宽度基准
 * @param baseHeightDp 默认高度基准
 */
@Composable
fun AdaptationDensity(
    baseWidthDp: Float = 411f,
    baseHeightDp: Float = 859f,
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val displayMetrics = remember { context.resources.displayMetrics }
    val fontScale = LocalDensity.current.fontScale

    // 判断是否为竖屏手机设备
    val isPortraitPhone = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    if (isPortraitPhone) {
        val density = remember {
            val widthDensity = displayMetrics.widthPixels / baseWidthDp
            val heightDensity = displayMetrics.heightPixels / baseHeightDp
            (widthDensity + heightDensity) / 2f
        }

        CompositionLocalProvider(
            LocalDensity provides Density(
                density = density,
                fontScale = fontScale
            )
        ) {
            content()
        }
    } else {
        content()
    }
}
