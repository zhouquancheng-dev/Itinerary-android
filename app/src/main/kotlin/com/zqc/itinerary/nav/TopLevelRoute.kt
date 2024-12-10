package com.zqc.itinerary.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.common.navigation.Home
import com.example.common.navigation.Message
import com.example.common.navigation.Profile
import com.example.common.navigation.ScenicSpot
import com.zqc.itinerary.R
import kotlinx.serialization.Serializable

@Immutable
data class TopLevelRoute<T : Any>(
    val route: T,
    @StringRes val label: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
)

val topLevelRoutes = listOf(
    TopLevelRoute(
        Home,
        R.string.bottomBar_home_label,
        R.drawable.ic_home_selected,
        R.drawable.ic_home_unselected
    ),
    TopLevelRoute(
        ScenicSpot,
        R.string.bottomBar_destination_label,
        R.drawable.ic_destination_selected,
        R.drawable.ic_destination_unselected
    ),
    TopLevelRoute(
        Message,
        R.string.bottomBar_message_label,
        R.drawable.ic_message_selected,
        R.drawable.ic_message_unselected
    ),
    TopLevelRoute(
        Profile,
        R.string.bottomBar_mine_label,
        R.drawable.ic_mine_selected,
        R.drawable.ic_mine_unselected
    )
)