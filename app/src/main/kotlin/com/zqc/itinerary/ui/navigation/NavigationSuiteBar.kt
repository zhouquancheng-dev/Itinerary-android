package com.zqc.itinerary.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import com.zqc.itinerary.nav.Screen
import com.zqc.itinerary.ui.ItineraryAppState
import com.zqc.itinerary.ui.isSameRoute
import com.zqc.itinerary.ui.isTopLevelDestinationInHierarchy

fun NavigationSuiteScope.navigationSuiteBar(
    appState: ItineraryAppState,
    currentDestination: NavDestination?,
    onNavigateToDestination: (Screen) -> Unit,
    totalUnreadCount: Long
) {
    appState.bottomNavItems.forEachIndexed { itemIndex, screen ->
        val selected = currentDestination.isTopLevelDestinationInHierarchy(screen)
        item(
            selected = selected,
            onClick = {
                if (currentDestination != null) {
                    if (!currentDestination.isSameRoute(screen)) {
                        onNavigateToDestination(screen)
                    }
                }
            },
            icon = {
                Badged(itemIndex, totalUnreadCount) {
                    Image(
                        painter = painterResource(screen.selectedIcon),
                        contentDescription = stringResource(screen.label)
                    )
                }
            },
            label = { Text(stringResource(screen.label)) }
        )
    }
}