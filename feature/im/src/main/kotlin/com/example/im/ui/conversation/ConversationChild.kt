package com.example.im.ui.conversation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.im.ui.ColorE57373
import com.example.im.ui.ColorFFAB91
import com.example.ui.components.symbols.rememberDelete
import com.example.ui.components.symbols.rememberVerticalAlignTop
import com.example.ui.components.swipe.DragValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationChild(
    isPinned: Boolean,
    draggableState: AnchoredDraggableState<DragValue>,
    onPinned: (Boolean) -> Unit,
    onDeleteConversation: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .width(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(ColorFFAB91)
                .clickable(
                    enabled = draggableState.currentValue == DragValue.End,
                    onClick = {
                        scope.launch { draggableState.snapTo(DragValue.Start) }
                        onPinned(!isPinned)
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = rememberVerticalAlignTop(),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
            Text(
                text = if (isPinned) "取消置顶" else "置顶",
                fontSize = 12.sp,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(ColorE57373)
                .clickable(
                    enabled = draggableState.currentValue == DragValue.End,
                    onClick = {
                        scope.launch { draggableState.snapTo(DragValue.Start) }
                        onDeleteConversation()
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = rememberDelete(),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
            Text(
                text = "删除聊天",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}