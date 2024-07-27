package com.zqc.itinerary.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.common.navigation.Home
import com.example.common.navigation.Message
import com.example.common.navigation.Profile
import com.example.common.navigation.ScenicSpot
import com.zqc.itinerary.R

@Immutable
sealed class Screen(
    val route: Any,
    @StringRes val label: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
) {
    data object HomeScreen : Screen(
        Home::class,
        R.string.bottomBar_home_label,
        R.drawable.ic_home_selected,
        R.drawable.ic_home_unselected
    )

    data object ScenicSpotScreen : Screen(
        ScenicSpot::class,
        R.string.bottomBar_destination_label,
        R.drawable.ic_destination_selected,
        R.drawable.ic_destination_unselected
    )

    data object MessageScreen : Screen(
        Message::class,
        R.string.bottomBar_message_label,
        R.drawable.ic_message_selected,
        R.drawable.ic_message_unselected
    )

    data object ProfileScreen : Screen(
        Profile::class,
        R.string.bottomBar_mine_label,
        R.drawable.ic_mine_selected,
        R.drawable.ic_mine_unselected
    )
}