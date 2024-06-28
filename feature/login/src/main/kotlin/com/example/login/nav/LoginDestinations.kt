package com.example.login.nav

sealed class LoginDestinations(val route: String) {

    data object LoginHome : LoginDestinations("LoginHome")

    data object VerifyCode : LoginDestinations("VerifyCode/{phoneNumber}") {
        fun onNavigateToRoute(phoneNumber: String) = "VerifyCode/$phoneNumber"
    }

}