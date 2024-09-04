package com.zqc.itinerary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.common.di.network.NetworkMonitor
import com.example.common.di.timezone.TimeZoneMonitor
import com.zqc.itinerary.nav.Screen
import com.zqc.itinerary.nav.Screen.HomeScreen
import com.zqc.itinerary.nav.Screen.MessageScreen
import com.zqc.itinerary.nav.Screen.ProfileScreen
import com.zqc.itinerary.nav.Screen.ScenicSpotScreen
import com.zqc.itinerary.nav.navigateToHome
import com.zqc.itinerary.nav.navigateToScenicSpot
import com.zqc.itinerary.nav.navigateToMessage
import com.zqc.itinerary.nav.navigateToProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): ItineraryAppState {
    return remember(
        navController,
        coroutineScope,
        networkMonitor
    ) {
        ItineraryAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            timeZoneMonitor = timeZoneMonitor,
        )
    }
}

@Stable
class ItineraryAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor
) {
    val bottomNavItems = listOf(HomeScreen, ScenicSpotScreen, MessageScreen, ProfileScreen)

    val currentDestinationAsState: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TimeZone.currentSystemDefault(),
        )

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: Screen<*>) {
        navController.popBackStack()

        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            HomeScreen -> navController.navigateToHome(topLevelNavOptions)
            ScenicSpotScreen -> navController.navigateToScenicSpot(topLevelNavOptions)
            MessageScreen -> navController.navigateToMessage(topLevelNavOptions)
            ProfileScreen -> navController.navigateToProfile(topLevelNavOptions)
        }
    }
}

internal fun NavDestination?.isTopLevelDestinationInHierarchy(destination: Screen<*>): Boolean {
    return this?.hierarchy?.any {
        it.route == destination.route!!::class.qualifiedName
    } == true
}

internal fun NavDestination?.isSameRoute(screen: Screen<*>): Boolean {
    return this?.route == screen.route!!::class.qualifiedName ||
            this?.hierarchy?.any { it.route == screen.route::class.qualifiedName } == true
}

internal fun NavDestination?.shouldShowBottomBar(bottomNavItems: List<Screen<*>>): Boolean {
    return this?.route?.let { route ->
        bottomNavItems.any { screen ->
            route == screen.route!!::class.qualifiedName
        }
    } == true
}
