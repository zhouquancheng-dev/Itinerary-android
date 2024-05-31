package com.zqc.itinerary.nav

import androidx.navigation.NavController

/**
 * 导航定义
 */
class NavMainActions(navController: NavController) {

    val startPageToRoute: (roure: String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(AppDestinations.StartScreen.route) {
                inclusive = true
            }
        }
    }

    val welcomePageToLoginRoute: () -> Unit = {
        navController.navigate(AppDestinations.LoginScreen.route) {
            popUpTo(AppDestinations.WelcomeScreen.route) {
                inclusive = true
            }
        }
    }

}

/**
 * 登录页导航定义
 */
class NavLoginActions(navController: NavController) {

    val upPress: () -> Unit = {
        navController.popBackStack()
    }

    val loginPageToMainRoute: () -> Unit = {
        navController.navigate(AppDestinations.MainScreen.route) {
            popUpTo(AppDestinations.LoginScreen.route) {
                inclusive = true
            }
        }
    }

    val loginPageToRoute: (route: String) -> Unit = { route ->
        navController.navigate(route)
    }

    val phonePageToVerifyCodeRoute: (countryCode: String, phoneNumber: String) -> Unit = { countryCode, phoneNumber ->
        navController.navigate(
            LoginPageDestinations.VerifyCodePage.onNavigateToRoute(countryCode, phoneNumber)
        )
    }

}