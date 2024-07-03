package com.zqc.itinerary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.im.MessageScreen
import com.example.mine.MineScreen
import com.example.ui.coil.LoadAsyncImage
import com.zqc.itinerary.R
import com.zqc.itinerary.nav.AppDestinations

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = AppDestinations.HomePage.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = AppDestinations.HomePage.route,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadAsyncImage(
                    model = R.drawable.iconmonstr_cat,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        composable(
            route = AppDestinations.DestinationPage.route,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Second Page")
            }
        }

        composable(
            route = AppDestinations.MessagePage.route,
        ) {
            MessageScreen()
        }

        composable(
            route = AppDestinations.MinePage.route,
        ) {
            MineScreen()
        }
    }
}