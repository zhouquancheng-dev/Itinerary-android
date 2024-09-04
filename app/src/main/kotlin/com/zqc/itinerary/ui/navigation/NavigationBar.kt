package com.zqc.itinerary.ui.navigation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavDestination
import com.example.common.data.Constants.TIM_TAG
import com.example.im.listener.conversation.TotalUnreadMessageCountChangedEvent
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.utils.subscribe
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import com.zqc.itinerary.nav.Screen
import com.zqc.itinerary.nav.Screen.HomeScreen
import com.zqc.itinerary.nav.Screen.MessageScreen
import com.zqc.itinerary.nav.Screen.ProfileScreen
import com.zqc.itinerary.nav.Screen.ScenicSpotScreen
import com.zqc.itinerary.ui.isSameRoute
import com.zqc.itinerary.ui.isTopLevelDestinationInHierarchy

@Composable
fun ItineraryBottomBar(
    modifier: Modifier = Modifier,
    destinations: List<Screen<*>>,
    onNavigateToDestination: (Screen<*>) -> Unit,
    currentDestination: NavDestination?
) {
    var totalUnreadCount by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        V2TIMManager.getConversationManager()
            .getTotalUnreadMessageCount(object : V2TIMValueCallback<Long?> {
                override fun onSuccess(aLong: Long?) {
                    if (aLong != null) {
                        totalUnreadCount = aLong
                    }
                }

                override fun onError(code: Int, desc: String) {
                    Log.i(TIM_TAG, "Error, code:$code, desc:$desc")
                }
            })
    }

    subscribe<TotalUnreadMessageCountChangedEvent> { event ->
        totalUnreadCount = event.totalUnreadCount
    }

    NavigationBar(modifier = modifier) {
        destinations.forEachIndexed { itemIndex, screen: Screen<*> ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(screen)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentDestination != null && !currentDestination.isSameRoute(screen)) {
                        onNavigateToDestination(screen)
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
}

@Preview(device = "id:pixel_6_pro", showBackground = true, locale = "zh-rCN")
@PreviewLightDark
@Composable
fun BottomBarPreview() {
    val bottomNavItems = listOf(HomeScreen, ScenicSpotScreen, MessageScreen, ProfileScreen)
    JetItineraryTheme {
        ItineraryBottomBar(
            destinations = bottomNavItems,
            onNavigateToDestination = {},
            currentDestination = null
        )
    }
}