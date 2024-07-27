package com.example.home.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.common.navigation.Home
import com.example.ui.R
import com.example.ui.coil.LoadAsyncImage

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
//                LoadAsyncImage(
//                    model = "https://images.unsplash.com/photo-1720440931331-bdc0e7af2045?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxmZWF0dXJlZC1waG90b3MtZmVlZHwxMjR8fHxlbnwwfHx8fHw%3D",
//                    modifier = Modifier
//                        .aspectRatio(1f),
//                    placeholder = R.drawable.module_ic_coil_placeholder
//                )
            }
        }
    }
}