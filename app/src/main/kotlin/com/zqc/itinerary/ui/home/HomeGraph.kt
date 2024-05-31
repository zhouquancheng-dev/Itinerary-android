package com.zqc.itinerary.ui.home

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.zqc.itinerary.nav.AppDestinations
import com.zqc.itinerary.nav.HomePageDestinations
import com.zqc.itinerary.nav.NavMainActions

fun NavGraphBuilder.homeGraph(
    mainActions: NavMainActions
) {
    navigation(
        startDestination = HomePageDestinations.HomePage.route,
        route = AppDestinations.MainScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(
            route = HomePageDestinations.HomePage.route,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("HomePage")
            }
        }

        composable(
            route = HomePageDestinations.DestinationPage.route,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("DestinationPage")
            }
        }

        composable(
            route = HomePageDestinations.MessagePage.route,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("MessagePage")
            }
        }

        composable(
            route = HomePageDestinations.MinePage.route,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("MinePage")
            }
        }
    }

}