package com.zqc.itinerary.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.common.navigation.Home
import com.example.common.navigation.Message
import com.example.common.navigation.ScenicSpot
import com.example.home.graph.homeGraph
import com.example.im.ui.conversation.ConversationHome
import com.example.im.vm.IMViewModel
import com.example.profile.graph.profileNavGraph

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = Home
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        homeGraph()

        composable<ScenicSpot> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            }
        }

        composable<Message> { backStackEntry ->
            val ivm = hiltViewModel<IMViewModel>(backStackEntry)
            ConversationHome(ivm)
        }

        profileNavGraph()
    }
}