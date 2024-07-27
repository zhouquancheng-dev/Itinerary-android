package com.zqc.itinerary.ui.navigation

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
internal fun Badged(
    itemIndex: Int,
    totalUnreadCount: Long,
    icon: @Composable () -> Unit
) {
    val totalCount = remember(totalUnreadCount) {
        if (totalUnreadCount > 99L) "99+" else totalUnreadCount.toString()
    }
    BadgedBox(
        badge = {
            if (itemIndex == 2) {
                if (totalUnreadCount != 0L) {
                    Badge {
                        Text(
                            text = totalCount,
                            modifier = Modifier.semantics {
                                contentDescription = "$totalUnreadCount new notifications"
                            }
                        )
                    }
                }
            }
        }
    ) {
        icon()
    }
}