package com.example.login.nav

import androidx.navigation.NavController

/**
 * 登录页导航定义
 */
class NavLoginActions(navController: NavController) {

    val upPress: () -> Unit = {
        navController.popBackStack()
    }

    val loginPageToMainRoute: () -> Unit = {
        navController.navigate("homeScreen") {
            popUpTo("loginScreen") {
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

/**
 * 登录页Screen各个 Page 路由定义
 * @param route 路由值
 */
sealed class LoginPageDestinations(val route: String) {
    /**
     * 登录页
     */
    data object LoginPage : LoginPageDestinations("loginPage")

    /**
     * 注册页
     */
    data object RegisterPage : LoginPageDestinations("registerPage")

    /**
     * 手机号输入页
     */
    data object PhoneNumberLoginPage : LoginPageDestinations("phoneNumberLoginPage")

    /**
     * 忘记密码页
     */
    data object ForgetPasswordPage : LoginPageDestinations("forgetPasswordPage")

    /**
     * 重置密码页
     */
    data object ResetPasswordPage : LoginPageDestinations("resetPasswordPage")

    /**
     * 短信验证码校验页
     */
    data object VerifyCodePage : LoginPageDestinations("verifyCodePage/{countryCode}/{phoneNumber}") {
        /**
         * @param countryCode 号码所属区号
         * @param phoneNumber 手机号码
         */
        fun onNavigateToRoute(countryCode: String, phoneNumber: String) =
            "verifyCodePage/$countryCode/$phoneNumber"
    }
}