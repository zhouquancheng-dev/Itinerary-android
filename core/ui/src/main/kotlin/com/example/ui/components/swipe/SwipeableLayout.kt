package com.example.ui.components.swipe

import android.content.res.Configuration
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Box滑动组件
 *
 * @author zqc
 * @param modifier 修饰符
 * @param enabled 是否启用滑动行为
 * @param swipeStyle 滑动时的手势行为 [SwipeStyle]
 * @param childContent 子内容 Composable
 * @param primaryContent 主内容 Composable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeBoxLayout(
    index: Int,
    currentSwipedIndex: MutableState<Int?>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    swipeStyle: SwipeStyle = SwipeStyle.EndToStart,
    childContent: @Composable (draggableState: AnchoredDraggableState<DragValue>) -> Unit,
    primaryContent: @Composable (draggableState: AnchoredDraggableState<DragValue>) -> Unit
) {
    val childWidth = remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Start,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = spring(DampingRatioLowBouncy),
            decayAnimationSpec = exponentialDecay(frictionMultiplier = 20f)
        )
    }

    LaunchedEffect(draggableState.currentValue) {
        if (draggableState.currentValue == DragValue.End) {
            currentSwipedIndex.value?.let { previousIndex ->
                if (previousIndex != index) {
                    currentSwipedIndex.value = index
                }
            } ?: run {
                currentSwipedIndex.value = index
            }
        } else if (draggableState.currentValue == DragValue.Start && currentSwipedIndex.value == index) {
            currentSwipedIndex.value = null
        }
    }

    LaunchedEffect(currentSwipedIndex.value) {
        if (currentSwipedIndex.value != index && draggableState.currentValue == DragValue.End) {
            draggableState.snapTo(DragValue.Start)
        }
    }

    Box(
        modifier = modifier
            .anchoredDraggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = enabled
            )
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .onSizeChanged { newSize ->
                    childWidth.floatValue = newSize.width.toFloat()
                    val childWidthPx = with(density) { childWidth.floatValue.toDp().toPx() }
                    val newAnchors = DraggableAnchors {
                        if (swipeStyle == SwipeStyle.EndToStart) {
                            DragValue.Start at 0f
                            DragValue.End at -childWidthPx
                        } else {
                            DragValue.Start at 0f
                            DragValue.End at childWidthPx
                        }
                    }
                    draggableState.updateAnchors(newAnchors)
                }
                .align(getChildAlign(swipeStyle))
        ) {
            childContent(draggableState)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        val offsetX = draggableState.requireOffset().roundToInt()
                        placeable.placeRelative(offsetX, 0)
                    }
                }
        ) {
            primaryContent(draggableState)
        }
    }
}

@Composable
private fun getChildAlign(swipeStyle: SwipeStyle) =
    if (swipeStyle == SwipeStyle.EndToStart) Alignment.CenterEnd else Alignment.CenterStart

/**
 * Row滑动组件
 *
 * @author zqc
 * @param modifier 修饰符
 * @param enabled 是否启用滑动行为
 * @param swipeStyle 滑动时的手势行为 [SwipeStyle]
 * @param childContent 子内容 Composable
 * @param primaryContent 主内容 Composable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeRowLayout(
    index: Int,
    currentSwipedIndex: MutableState<Int?>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    swipeStyle: SwipeStyle = SwipeStyle.EndToStart,
    childContent: @Composable (draggableState: AnchoredDraggableState<DragValue>) -> Unit,
    primaryContent: @Composable (draggableState: AnchoredDraggableState<DragValue>) -> Unit
) {
    val density = LocalDensity.current
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Start,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = spring(),
            decayAnimationSpec = exponentialDecay(frictionMultiplier = 20f)
        )
    }

    val childWidthState = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(childWidthState.floatValue, swipeStyle) {
        val childWidthPx = with(density) { childWidthState.floatValue.toDp().toPx() }
        val newAnchors = DraggableAnchors {
            if (swipeStyle == SwipeStyle.EndToStart) {
                DragValue.Start at 0f
                DragValue.End at -childWidthPx
            } else {
                DragValue.Start at 0f
                DragValue.End at childWidthPx
            }
        }
        draggableState.updateAnchors(newAnchors)
    }

    LaunchedEffect(draggableState.currentValue) {
        if (draggableState.currentValue == DragValue.End) {
            currentSwipedIndex.value?.let { previousIndex ->
                if (previousIndex != index) {
                    currentSwipedIndex.value = index
                }
            } ?: run {
                currentSwipedIndex.value = index
            }
        } else if (draggableState.currentValue == DragValue.Start && currentSwipedIndex.value == index) {
            currentSwipedIndex.value = null
        }
    }

    LaunchedEffect(currentSwipedIndex.value) {
        if (currentSwipedIndex.value != index && draggableState.currentValue == DragValue.End) {
            draggableState.snapTo(DragValue.Start)
        }
    }

    SubcomposeLayout(
        modifier = modifier
            .anchoredDraggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = enabled
            )
            .clipToBounds()
    ) { constraints ->
        val childConstraints = constraints.copy(minWidth = 0, maxWidth = constraints.maxWidth)
        val childPlaceable = subcompose("child") {
            childContent(draggableState)
        }.first().measure(childConstraints)

        childWidthState.floatValue = childPlaceable.width.toFloat()

        val primaryPlaceable = subcompose("primary") {
            primaryContent(draggableState)
        }.first().measure(constraints)

        layout(constraints.minWidth, constraints.minHeight) {
            val offsetX = draggableState.offset.dp.value.takeIf { !it.isNaN() }?.roundToInt() ?: 0

            primaryPlaceable.placeRelative(offsetX, 0)

            if (swipeStyle == SwipeStyle.EndToStart) {
                childPlaceable.placeRelative(constraints.maxWidth + offsetX, 0)
            } else {
                childPlaceable.placeRelative(offsetX - childPlaceable.width, 0)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    name = "自定义滑动组件使用示例",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    locale = "zh-rCN",
    showBackground = true,
    device = "id:pixel_6_pro"
)
@Composable
private fun SwipeLayoutSample() {
    val currentSwipedIndex = remember { mutableStateOf<Int?>(null) }
    val items = listOf("Item 1", "Item 2", "Item 3")

    LazyColumn {
        itemsIndexed(items) { index, item ->
            SwipeBoxLayout(
                index = index,
                currentSwipedIndex = currentSwipedIndex,
                modifier = Modifier.fillMaxWidth(),
                swipeStyle = SwipeStyle.EndToStart,
                childContent = { _ ->
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(300.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(0)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = null,
                                modifier = Modifier.size(45.dp)
                            )
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(0)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(45.dp)
                            )
                        }
                    }
                },
                primaryContent = { _ ->
                    Text(item, modifier = Modifier.padding(16.dp))
                }
            )
        }
    }
}