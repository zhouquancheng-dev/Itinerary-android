package com.example.im.ui.conversation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.util.TimestampUtils.formatTimestamp
import com.example.im.R
import com.example.im.ui.ColorFFFA5151
import com.example.im.utils.getLastMessageContent
import com.example.ui.coil.LoadAsyncImage
import com.example.ui.components.HorizontalSpacer
import com.example.ui.components.resolveColor
import com.example.ui.components.swipe.DragValue
import com.example.ui.theme.Color14FFFFFF
import com.example.ui.theme.ColorFFF2F2F2
import com.tencent.imsdk.v2.V2TIMConversation
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationPrimary(
    conversation: V2TIMConversation,
    draggableState: AnchoredDraggableState<DragValue>,
    currentSwipedIndex: MutableState<Int?>,
    onChatUI: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val resetAllStates = {
        scope.launch {
            draggableState.snapTo(DragValue.Start)
            currentSwipedIndex.value = null
        }
    }

    if (conversation.lastMessage == null) return

    val formatTimestamp = remember(conversation.lastMessage.timestamp) {
        formatTimestamp(conversation.lastMessage.timestamp)
    }

    val surfaceColor by animateColorAsState(
        targetValue = if (conversation.isPinned) resolveColor(ColorFFF2F2F2, Color14FFFFFF) else MaterialTheme.colorScheme.background,
        label = "bg"
    )

    Surface(
        onClick = {
            if (draggableState.currentValue == DragValue.End) {
                resetAllStates()
            } else {
                resetAllStates()
                onChatUI()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LoadAsyncImage(
                model = conversation.faceUrl,
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholderResId = R.drawable.ic_default_face,
                errorResId = R.drawable.ic_default_face
            )

            HorizontalSpacer(10.dp)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(vertical = 3.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = conversation.showName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                )

                val lastMessage = conversation.lastMessage
                val messageContent = getLastMessageContent(lastMessage.elemType, lastMessage.textElem)
                Text(
                    text = messageContent,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            HorizontalSpacer(8.dp)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 3.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTimestamp,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                )

                val unreadCount = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString()
                if (conversation.unreadCount != 0) {
                    Text(
                        text = unreadCount,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .semantics {
                                contentDescription = "$unreadCount new notifications"
                            }
                            .drawBehind {
                                drawCircle(
                                    color = ColorFFFA5151,
                                    radius = 11.dp.toPx()
                                )
                            }
                            .wrapContentSize(Alignment.Center)
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }
}