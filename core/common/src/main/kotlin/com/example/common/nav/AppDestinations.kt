package com.example.common.nav

/**
 * APP全局 Screen 路由定义
 * @param route 路由值
 */
sealed class AppDestinations(val route: String) {
    /**
     * 主页Screen
     */
    data object MainScreen : AppDestinations("homeScreen")

    /**
     * 登录页Screen
     */
    data object LoginScreen : AppDestinations("loginScreen")
}