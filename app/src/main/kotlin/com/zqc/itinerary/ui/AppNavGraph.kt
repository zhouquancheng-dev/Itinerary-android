package com.zqc.itinerary.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.common.navigation.Home
import com.example.common.navigation.Message
import com.example.common.navigation.Profile
import com.example.common.navigation.ScenicSpot
import com.example.common.util.startActivity
import com.example.home.ui.HomeScreen
import com.example.home.vm.HomeViewModel
import com.example.im.ui.conversation.ConversationHomeScreen
import com.example.im.vm.IMViewModel
import com.example.profile.activity.ProfileInfoActivity
import com.example.profile.ui.ProfileHomeScreen
import com.example.profile.vm.ProfileViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = Home
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<Home> { backStackEntry ->
            val hvm = hiltViewModel<HomeViewModel>(backStackEntry)
            HomeScreen(hvm)
        }

        composable<ScenicSpot> {

        }

        composable<Message> { backStackEntry ->
            val ivm = hiltViewModel<IMViewModel>(backStackEntry)
            ConversationHomeScreen(ivm)
        }

        composable<Profile>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val profileVm = hiltViewModel<ProfileViewModel>(backStackEntry)
            ProfileHomeScreen(
                profileVm = profileVm,
                onProfileInfo = {
                    startActivity<ProfileInfoActivity>(context)
                }
            )
        }
    }
}