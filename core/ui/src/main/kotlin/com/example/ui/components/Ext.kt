package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.common.util.ext.isDarkMode
import com.example.ui.theme.DarkGreenGray95
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.shimmer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun HorizontalSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun resolveColor(dayColor: Color, nightColor: Color): Color {
    val context = LocalContext.current
    return if (context.isDarkMode()) {
        nightColor
    } else {
        dayColor
    }
}

@Composable
fun resolveColorResource(dayColorRes: Int, nightColorRes: Int): Color {
    val context = LocalContext.current
    return if (context.isDarkMode()) {
        colorResource(nightColorRes)
    } else {
        colorResource(dayColorRes)
    }
}

private const val MIN_CLICK_DELAY_TIME = 300
private var lastClickTime: Long = 0

val isFastClick
    get(): Boolean {
        var flag = false
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime <= MIN_CLICK_DELAY_TIME) {
            flag = true
        }
        lastClickTime = curClickTime
        return flag
    }

inline fun Modifier.click(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    showRipple: Boolean = true,
    role: Role? = null,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    rippleColor: Color = Color.Unspecified,
    crossinline onClick: () -> Unit
) = composed {
    clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = if (showRipple) ripple(color = rippleColor, radius = radius, bounded = bounded) else null,
        role = role,
        onClickLabel = onClickLabel
    ) {
        if (!isFastClick) {
            onClick()
        }
    }
}

/**
 * 自定义扩展 Modifier.clickable()
 * 无涟漪动画效果的单击点击事件
 * @param onClick 当用户点击元素时将被调用
 */
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        if (!isFastClick) {
            onClick()
        }
    }
}

fun Modifier.placeholder(
    isLoading: Boolean,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape? = RoundedCornerShape(5.dp),
    shimmerAnimation: Boolean = true
): Modifier = composed {
    val highlight = if (shimmerAnimation) {
        PlaceholderHighlight.shimmer(highlightColor = MaterialTheme.colorScheme.onTertiary)
    } else {
        null
    }
    val specifiedBackgroundColor = backgroundColor.takeOrElse { DarkGreenGray95.copy(0.8f) }
    placeholder(
        color = specifiedBackgroundColor,
        visible = isLoading,
        shape = shape,
        highlight = highlight
    )
}

fun Modifier.bounceScrollEffect(
    maxOffsetY: Float = 500f,
    coroutineScope: CoroutineScope,
    animatedOffsetY: Animatable<Float, AnimationVector1D>
): Modifier = nestedScroll(object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        val newOffsetY = animatedOffsetY.value + delta

        // 仅在偏移量在 [-maxOffsetY, maxOffsetY] 范围内时更新
        return if (newOffsetY in -maxOffsetY..maxOffsetY) {
            coroutineScope.launch {
                animatedOffsetY.snapTo(newOffsetY)
            }
            Offset(0f, delta)
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        coroutineScope.launch {
            animatedOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring()
            )
        }
        return super.onPostFling(consumed, available)
    }
})

@Composable
fun BounceScrollableContent(
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val maxOffsetY by remember { mutableFloatStateOf(with(density) { 300.dp.toPx() }) }
    val coroutineScope = rememberCoroutineScope()
    val animatedOffsetY = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(x = 0, y = animatedOffsetY.value.roundToInt()) }
            .nestedScroll(object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffsetY = animatedOffsetY.value + delta

                    // 仅在偏移量在 [-maxOffsetY, maxOffsetY] 范围内时更新
                    return if (newOffsetY in -maxOffsetY..maxOffsetY) {
                        coroutineScope.launch {
                            animatedOffsetY.snapTo(newOffsetY)
                        }
                        Offset(0f, delta)
                    } else {
                        Offset.Zero
                    }
                }

                override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                    // 回弹到原位
                    coroutineScope.launch {
                        animatedOffsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        )
                    }
                    return super.onPostFling(consumed, available)
                }
            })
    ) {
        content()
    }
}
