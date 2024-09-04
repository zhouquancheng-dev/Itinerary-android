package com.zqc.itinerary.nav

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.common.navigation.Home
import com.example.common.navigation.Message
import com.example.common.navigation.Profile
import com.example.common.navigation.ScenicSpot

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(route = Home, navOptions = navOptions)
}

fun NavController.navigateToScenicSpot(navOptions: NavOptions? = null) {
    this.navigate(route = ScenicSpot, navOptions = navOptions)
}

fun NavController.navigateToMessage(navOptions: NavOptions? = null) {
    this.navigate(route = Message, navOptions = navOptions)
}

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(route = Profile, navOptions = navOptions)
}