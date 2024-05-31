package com.zqc.itinerary.nav

/**
 * APP全局 Screen 路由定义
 * @param route 路由值
 */
sealed class AppDestinations(val route: String) {
    /**
     * 启动页Screen
     */
    data object StartScreen : AppDestinations("startScreen")

    /**
     * 欢迎页Screen
     */
    data object WelcomeScreen : AppDestinations("welcomeScreen")

    /**
     * 主页Screen
     */
    data object MainScreen : AppDestinations("homeScreen")

    /**
     * 登录页Screen
     */
    data object LoginScreen : AppDestinations("loginScreen")
}

/**
 * 主页Screen各个 Page 路由定义
 * @param route 路由值
 */
sealed class HomePageDestinations(val route: String) {
    /**
     * 首页
     */
    data object HomePage : HomePageDestinations("homePage")

    /**
     * 目的地页
     */
    data object DestinationPage : HomePageDestinations("destinationPage")

    /**
     * 消息页
     */
    data object MessagePage : HomePageDestinations("messagePage")

    /**
     * 我的页
     */
    data object MinePage : HomePageDestinations("minePage")
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