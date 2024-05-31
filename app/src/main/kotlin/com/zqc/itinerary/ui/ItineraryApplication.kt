package com.zqc.itinerary.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zqc.itinerary.ui.home.component.ItineraryBottomBar
import com.zqc.itinerary.ui.home.component.bottomNavItemsList
import com.example.ui.theme.JetItineraryTheme

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
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
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (shouldShowBottomBar) {
                    ItineraryBottomBar(navController)
                }
            }
        ) { innerPadding ->
            AppNavGraph(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                navController = navController
            )
        }
    }
}