package com.zqc.itinerary.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import com.zqc.itinerary.nav.TopLevelRoute
import com.zqc.itinerary.ui.ItineraryAppState
import com.zqc.itinerary.ui.isSameRoute
import com.zqc.itinerary.ui.isTopLevelDestinationInHierarchy

fun NavigationSuiteScope.navigationSuiteBar(
    appState: ItineraryAppState,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelRoute<*>) -> Unit,
    totalUnreadCount: Long
) {
    appState.bottomNavItems.forEachIndexed { itemIndex, topLevelRoute ->
        val selected = currentDestination.isTopLevelDestinationInHierarchy(topLevelRoute)
        item(
            selected = selected,
            onClick = {
                if (currentDestination != null && !currentDestination.isSameRoute(topLevelRoute)) {
                    onNavigateToDestination(topLevelRoute)
                }
            },
            icon = {
                Badged(itemIndex, totalUnreadCount) {
                    Image(
                        painter = painterResource(topLevelRoute.selectedIcon),
                        contentDescription = stringResource(topLevelRoute.label)
                    )
                }
            },
            label = { Text(stringResource(topLevelRoute.label)) }
        )
    }
}