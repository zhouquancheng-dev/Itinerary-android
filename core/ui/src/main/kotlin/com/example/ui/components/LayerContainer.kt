package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Popup

@Composable
fun PopupWithMeasuredPosition(
    modifier: Modifier = Modifier,
    offset: IntOffset,
    onDismissRequest: () -> Unit,
    popupContent: @Composable BoxScope.() -> Unit
) {
    BoxWithConstraints {
        Popup(
            alignment = Alignment.TopStart,
            offset = offset,
            onDismissRequest = onDismissRequest
        ) {
            Box(
                modifier = modifier
                    .background(Color.Gray)
                    .sizeIn(maxWidth = maxWidth, maxHeight = maxHeight)
            ) {
                popupContent()
            }
        }
    }
}

@Composable
private fun Content() {
    var attach by remember { mutableStateOf(false) }
    var offset: IntOffset? by remember { mutableStateOf(null) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(50) { index ->
            ListItem(
                text = "Item $index",
                onOffset = {
                    offset = it
                    attach = true
                }
            )
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    text: String,
    onOffset: (IntOffset?) -> Unit
) {
    val onOffsetUpdated by rememberUpdatedState(onOffset)
    var coordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .onGloballyPositioned { coordinates = it }
            .pointerInput(Unit) {
                detectTapGestures {
                    // 获取触摸点相对于Window的坐标
                    val offset = coordinates?.localToWindow(it)?.round()
                    // 回调坐标点
                    onOffsetUpdated(offset)
                }
            }
    ) {
        Text(text = text, modifier = Modifier.align(Alignment.Center))
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}
