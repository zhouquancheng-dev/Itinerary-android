package com.zqc.itinerary.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.common.util.DataStoreUtils
import com.zqc.itinerary.nav.AppDestinations
import com.zqc.itinerary.nav.NavLoginActions
import com.zqc.itinerary.nav.NavMainActions
import com.zqc.itinerary.ui.home.homeGraph
import com.zqc.itinerary.ui.login.loginGraph
import com.zqc.itinerary.ui.login.obj.LoginState
import com.zqc.itinerary.ui.splash.AppSplashScreen
import com.zqc.itinerary.ui.welcome.WelcomeScreen

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = AppDestinations.StartScreen.route
) {
    val mainActions = remember(navController) { NavMainActions(navController) }
    val loginActions = remember(navController) { NavLoginActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(AppDestinations.StartScreen.route) {
            AppSplashScreen {
                if (isFirstTimeLaunch()) {
                    mainActions.startPageToRoute(AppDestinations.WelcomeScreen.route)
                } else {
                    if (LoginState.login) {
                        mainActions.startPageToRoute(AppDestinations.MainScreen.route)
                    } else {
                        mainActions.startPageToRoute(AppDestinations.LoginScreen.route)
                    }
                }
            }
        }

        composable(AppDestinations.WelcomeScreen.route) {
            WelcomeScreen {
                DataStoreUtils.putBooleanSync("IS_FIRST_TIME_LAUNCH", false)
                mainActions.welcomePageToLoginRoute()
            }
        }

        homeGraph(mainActions)

        loginGraph(navController, loginActions)
    }
}

/**
 * 返回是否首次打开APP
 */
private fun isFirstTimeLaunch(): Boolean {
    return DataStoreUtils.getBooleanSync("IS_FIRST_TIME_LAUNCH", true)
}