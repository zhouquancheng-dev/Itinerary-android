package com.example.ui.view

import android.content.Context

/**
 * 获取当前是否为深色模式
 * 深色模式的值为: 0x21
 * 浅色模式的值为: 0x11
 * @return true 为深色模式   false 浅色模式
 */
fun Context.isDarkMode(): Boolean {
    return resources.configuration.uiMode == 0x21
}
