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
@Serializable
sealed class Screen<T>(
    val route: T,
    @StringRes val label: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
) {
    @Serializable
    data object HomeScreen : Screen<Home>(
        route = Home,
        label = R.string.bottomBar_home_label,
        selectedIcon = R.drawable.ic_home_selected,
        unselectedIcon = R.drawable.ic_home_unselected
    )

    @Serializable
    data object ScenicSpotScreen : Screen<ScenicSpot>(
        route = ScenicSpot,
        label = R.string.bottomBar_destination_label,
        selectedIcon = R.drawable.ic_destination_selected,
        unselectedIcon = R.drawable.ic_destination_unselected
    )

    @Serializable
    data object MessageScreen : Screen<Message>(
        route = Message,
        label = R.string.bottomBar_message_label,
        selectedIcon = R.drawable.ic_message_selected,
        unselectedIcon = R.drawable.ic_message_unselected
    )

    @Serializable
    data object ProfileScreen : Screen<Profile>(
        route = Profile,
        label = R.string.bottomBar_mine_label,
        selectedIcon = R.drawable.ic_mine_selected,
        unselectedIcon = R.drawable.ic_mine_unselected
    )
}
