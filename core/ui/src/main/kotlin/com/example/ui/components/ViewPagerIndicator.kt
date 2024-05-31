package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 自定义水平滑动的 ViewPager 指示器
 * @param pagerState pager状态
 * @param pageCount 页数
 * @param modifier Modifier
 * @param activeColor 活动页面指示器的颜色
 * @param inactiveColor 非活动页面指示器的颜色
 * @param activeSize 活动页面指示器的大小
 * @param activeSize 非活动页面指示器的大小
 * @param indicatorSpacing 指示器间隔
 */
@ExperimentalFoundationApi
@Composable
fun HorizontalPagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.inversePrimary,
    inactiveColor: Color = MaterialTheme.colorScheme.onBackground,
    activeSize: Dp = 3.dp,
    inactiveSize: Dp = activeSize,
    indicatorSpacing: Dp = 15.dp
) {
    HorizontalIndicator(
        pagerState = pagerState,
        pageCount = pageCount,
        modifier = modifier,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        activeSize = activeSize,
        inactiveSize = inactiveSize,
        indicatorSpacing = indicatorSpacing
    )
}

/**
 * 水平指示器
 */
@ExperimentalFoundationApi
@Composable
private fun HorizontalIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color,
    inactiveColor: Color,
    activeSize: Dp,
    inactiveSize: Dp,
    indicatorSpacing: Dp
) {
    val activeRadiusPx = with(LocalDensity.current) { activeSize.toPx() }
    val inactiveRadiusPx = with(LocalDensity.current) { inactiveSize.toPx() }
    val spacingPx = with(LocalDensity.current) { indicatorSpacing.toPx() }

    val radiusAnimatables = remember(pageCount) {
        List(pageCount) { index ->
            Animatable(if (index == pagerState.currentPage) activeRadiusPx else inactiveRadiusPx)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { currentPage ->
                radiusAnimatables.forEachIndexed { index, animate ->
                    val targetValue = if (index == currentPage) activeRadiusPx else inactiveRadiusPx
                    animate.animateTo(
                        targetValue = targetValue,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            }
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val totalIndicatorWidth = pageCount * (inactiveRadiusPx * 2 + spacingPx) - spacingPx + activeRadiusPx * 2
        val start = (canvasWidth - totalIndicatorWidth) / 2

        for (index in 0 until pageCount) {
            val indicatorColor = if (index == pagerState.currentPage) activeColor else inactiveColor
            val indicatorRadius = radiusAnimatables[index].value

            drawCircle(
                color = indicatorColor,
                radius = indicatorRadius,
                center = Offset(
                    start + index * (inactiveRadiusPx * 2 + spacingPx) + (if (index == pagerState.currentPage) activeRadiusPx else inactiveRadiusPx),
                    canvasHeight / 2
                )
            )
        }
    }
}