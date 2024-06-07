package com.zqc.itinerary.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.JetItineraryTheme
import com.zqc.itinerary.nav.bottomNavItemsList
import com.zqc.itinerary.ui.component.ItineraryBottomBar

@Composable
fun ItineraryApplication() {
    val navController = rememberNavController()
    val bottomBarRoutes = bottomNavItemsList.map { it.route }
    val shouldShowBottomBar =
        navController.currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes

    JetItineraryTheme {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            bottomBar = {
                if (shouldShowBottomBar) {
                    ItineraryBottomBar(navController)
                }
            }
        ) { innerPadding ->
            AppNavGraph(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                navController = navController
            )
        }
    }
}
