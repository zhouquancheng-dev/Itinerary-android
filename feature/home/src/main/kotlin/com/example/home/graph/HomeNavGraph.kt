package com.example.home.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.common.navigation.Home

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(Home, navOptions)
}

fun NavGraphBuilder.homeGraph() {
    navigation<Home>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

            }
        }
    }
}