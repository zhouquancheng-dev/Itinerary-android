package com.example.login

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.login.nav.LoginHome
import com.example.login.nav.VerifyCode
import com.example.login.ui.LoginScreen
import com.example.login.ui.VerifyCodeScreen
import com.example.login.vm.LoginViewModel

@Composable
fun LoginNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = LoginHome,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<LoginHome> { backStackEntry ->
            val loginViewModel = hiltViewModel<LoginViewModel>(backStackEntry)
            LoginScreen(loginViewModel) { phoneNumber ->
                navController.navigate(VerifyCode(phoneNumber))
            }
        }

        composable<VerifyCode>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = LinearEasing)
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = LinearEasing)
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) { backStackEntry ->
            val loginViewModel = hiltViewModel<LoginViewModel>(backStackEntry)
            val verify = backStackEntry.toRoute<VerifyCode>()
            VerifyCodeScreen(
                loginViewModel = loginViewModel,
                phoneNumber = verify.phoneNumber,
                onPress = { navController.navigateUp() }
            )
        }
    }
}