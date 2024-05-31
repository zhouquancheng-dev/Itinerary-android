package com.zqc.itinerary.ui.home.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zqc.itinerary.R
import com.zqc.itinerary.nav.HomePageDestinations
import com.example.ui.theme.JetItineraryTheme

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val icon: Int
) {
    data object Home : Screen(
        HomePageDestinations.HomePage.route,
        R.string.bottomBar_home_label,
        R.drawable.ic_home_selected
    )

    data object Destination : Screen(
        HomePageDestinations.DestinationPage.route,
        R.string.bottomBar_destination_label,
        R.drawable.ic_destination_selected
    )

    data object Message : Screen(
        HomePageDestinations.MessagePage.route,
        R.string.bottomBar_message_label,
        R.drawable.ic_message_selected
    )

    data object Mine : Screen(
        HomePageDestinations.MinePage.route,
        R.string.bottomBar_mine_label,
        R.drawable.ic_mine_selected
    )
}

val bottomNavItemsList = listOf(
    Screen.Home,
    Screen.Destination,
    Screen.Message,
    Screen.Mine
)

@ExperimentalMaterial3Api
@Composable
fun ItineraryBottomBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavItemsList.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (index == 2) {
                                Badge {
                                    val badgeNumber = "99+"
                                    Text(
                                        badgeNumber,
                                        modifier = Modifier.semantics {
                                            contentDescription = "$badgeNumber new notifications"
                                        }
                                    )
                                }
                            }
                        }
                    ) {
                        Image(
                            painter = painterResource(item.icon),
                            contentDescription = stringResource(item.resourceId)
                        )
                    }
                },
                label = { Text(text = stringResource(item.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.popBackStack()
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"
)
@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"

)
@Composable
fun ItineraryBottomBarPreview() {
    JetItineraryTheme {
        ItineraryBottomBar(rememberNavController())
    }
}