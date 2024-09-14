package com.zqc.itinerary.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.common.navigation.Home
import com.example.common.navigation.Message
import com.example.common.navigation.Profile
import com.example.common.navigation.ScenicSpot
import com.example.common.util.startActivity
import com.example.im.ui.conversation.ConversationHome
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
        composable<Home> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
//                PinchZoomRotateImage(
//                    imageModel = R.drawable.a,
//                    modifier = Modifier.aspectRatio(1f)
//                )
            }
        }

        composable<ScenicSpot> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
//                LoadAsyncImage(model = "https://inews.gtimg.com/om_bt/O6SG7dHjdG0kWNyWz6WPo2_3v6A6eAC9ThTazwlKPO1qMAA/641")
//                Text(text = "ScenicSpot")
            }
        }

        composable<Message> { backStackEntry ->
            val ivm = hiltViewModel<IMViewModel>(backStackEntry)
            ConversationHome(ivm)
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