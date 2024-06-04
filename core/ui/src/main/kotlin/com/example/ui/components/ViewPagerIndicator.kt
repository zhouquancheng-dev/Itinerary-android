package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
 * @param inactiveSize 非活动页面指示器的大小
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
    with(LocalDensity.current) {
        val activeSizePx = activeSize.toPx()
        val inactiveSizePx = inactiveSize.toPx()
        val spacingPx = indicatorSpacing.toPx()

        val indicatorRadius = { index: Int ->
            if (index == pagerState.currentPage) activeSizePx else inactiveSizePx
        }

        Canvas(modifier = modifier) {
            val totalWidth = (pageCount - 1) * (inactiveSizePx * 2 + spacingPx) + activeSizePx * 2
            val startX = (size.width - totalWidth) / 2
            val centerY = size.height / 2

            for (index in 0 until pageCount) {
                val radius = indicatorRadius(index)
                val color = if (index == pagerState.currentPage) activeColor else inactiveColor
                val offsetX = startX + index * (inactiveSizePx * 2 + spacingPx) +
                        if (index == pagerState.currentPage) activeSizePx else inactiveSizePx

                drawCircle(
                    color = color,
                    radius = radius,
                    center = Offset(offsetX, centerY)
                )
            }
        }
    }
}
