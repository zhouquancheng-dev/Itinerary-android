package com.zqc.itinerary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.common.di.network.NetworkMonitor
import com.example.common.di.timezone.TimeZoneMonitor
import com.example.common.navigation.Home
import com.example.common.navigation.Message
import com.example.common.navigation.Profile
import com.example.common.navigation.ScenicSpot
import com.zqc.itinerary.nav.TopLevelRoute
import com.zqc.itinerary.nav.navigateToHome
import com.zqc.itinerary.nav.navigateToScenicSpot
import com.zqc.itinerary.nav.navigateToMessage
import com.zqc.itinerary.nav.navigateToProfile
import com.zqc.itinerary.nav.topLevelRoutes
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
    val bottomNavItems = topLevelRoutes

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
     * @param topLevelRoute: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelRoute : TopLevelRoute<*>) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelRoute.route) {
            Home -> navController.navigateToHome(topLevelNavOptions)
            ScenicSpot -> navController.navigateToScenicSpot(topLevelNavOptions)
            Message -> navController.navigateToMessage(topLevelNavOptions)
            Profile -> navController.navigateToProfile(topLevelNavOptions)
        }
    }
}

internal fun NavDestination?.isTopLevelDestinationInHierarchy(topLevelRoute: TopLevelRoute<*>): Boolean {
    return this?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true
}

internal fun NavDestination?.isSameRoute(topLevelRoute: TopLevelRoute<*>): Boolean {
    return this?.route == topLevelRoute.route::class.qualifiedName ||
            this?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true
}

internal fun NavDestination?.shouldShowBottomBar(bottomNavItems: List<TopLevelRoute<*>>): Boolean {
    return this?.route?.let { route ->
        bottomNavItems.any { screen ->
            route == screen.route::class.qualifiedName
        }
    } == true
}
