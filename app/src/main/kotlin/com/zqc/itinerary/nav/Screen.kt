package com.zqc.itinerary.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.zqc.itinerary.R

val bottomNavItemsList = listOf(
    Screen.Home,
    Screen.Destination,
    Screen.Message,
    Screen.Mine
)

@Immutable
sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unSelectedIcon: Int
) {
    data object Home : Screen(
        AppDestinations.HomePage.route,
        R.string.bottomBar_home_label,
        R.drawable.ic_home_selected,
        R.drawable.ic_home_unselected
    )

    data object Destination : Screen(
        AppDestinations.DestinationPage.route,
        R.string.bottomBar_destination_label,
        R.drawable.ic_destination_selected,
        R.drawable.ic_destination_unselected
    )

    data object Message : Screen(
        AppDestinations.MessagePage.route,
        R.string.bottomBar_message_label,
        R.drawable.ic_message_selected,
        R.drawable.ic_message_unselected
    )

    data object Mine : Screen(
        AppDestinations.MinePage.route,
        R.string.bottomBar_mine_label,
        R.drawable.ic_mine_selected,
        R.drawable.ic_mine_unselected
    )
}