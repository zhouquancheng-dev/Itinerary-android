package com.example.profile.graph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.common.navigation.Profile
import com.example.profile.ui.MineScreen
import com.example.profile.vm.ProfileViewModel

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(route = Profile, navOptions = navOptions)
}

fun NavGraphBuilder.profileNavGraph() {
    navigation<Profile>(startDestination = ProfileRoute) {
        composable<ProfileRoute> { backStackEntry ->
            val profileVm = hiltViewModel<ProfileViewModel>(backStackEntry)
            MineScreen(profileVm)
        }

        composable<ProfileSettings> {

        }
    }
}