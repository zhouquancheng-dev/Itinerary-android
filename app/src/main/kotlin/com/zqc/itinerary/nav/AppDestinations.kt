package com.zqc.itinerary.nav

sealed class AppDestinations(val route: String) {
    /**
     * 首页
     */
    data object HomePage : AppDestinations("HomePage")

    /**
     * 目的地页
     */
    data object DestinationPage : AppDestinations("DestinationPage")

    /**
     * 消息页
     */
    data object MessagePage : AppDestinations("MessagePage")

    /**
     * 我的页
     */
    data object MinePage : AppDestinations("MinePage")
}