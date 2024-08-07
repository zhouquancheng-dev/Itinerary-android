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
import com.example.home.navigation.navigateToHome
import com.zqc.itinerary.nav.Screen
import com.zqc.itinerary.nav.Screen.HomeScreen
import com.zqc.itinerary.nav.Screen.MessageScreen
import com.zqc.itinerary.nav.Screen.ProfileScreen
import com.zqc.itinerary.nav.Screen.ScenicSpotScreen
import com.zqc.itinerary.nav.navigateToProfile
import com.zqc.itinerary.nav.navigateToScenicSpot
import com.zqc.itinerary.nav.navigateToMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

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
    val bottomNavItems: List<Screen> = listOf(HomeScreen, ScenicSpotScreen, MessageScreen, ProfileScreen)

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: Boolean
        @Composable get() = currentDestination?.isTopLevelDestinationInHierarchy(HomeScreen) == true

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
    fun navigateToTopLevelDestination(topLevelDestination: Screen) {
        navController.popBackStack()

        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
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

private val routeCache = mutableMapOf<KClass<*>, String>()

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
private fun <T : Any> NavDestination.hasRoute(route: KClass<T>): Boolean {
    val routeName = routeCache.getOrPut(route) {
        route.serializer().descriptor.serialName
    }
    return routeName == this.route
}

internal fun NavDestination?.isTopLevelDestinationInHierarchy(destination: Screen): Boolean {
    return this?.hierarchy?.any { navDestination ->
        when (destination.route) {
            is KClass<*> -> navDestination.hasRoute(destination.route)
            else -> false
        }
    } == true
}

fun NavDestination.isSameRoute(destination: Screen): Boolean {
    return when (destination.route) {
        is KClass<*> -> this.hasRoute(destination.route)
        else -> false
    }
}