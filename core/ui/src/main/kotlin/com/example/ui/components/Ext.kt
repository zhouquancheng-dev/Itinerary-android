package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.common.util.ClickUtils.isFastClick
import com.example.ui.theme.DarkGreenGray95
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.shimmer

@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun HorizontalSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}

inline fun Modifier.click(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    showRipple: Boolean = true,
    role: Role? = null,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    rippleColor: Color = Color.Unspecified,
    debounce: Boolean = false,
    crossinline onClick: () -> Unit
) = composed {
    clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = if (showRipple) ripple(color = rippleColor, radius = radius, bounded = bounded) else null,
        role = role,
        onClickLabel = onClickLabel
    ) {
        if (!debounce || !isFastClick()) {
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
        onClick()
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
    val specifiedBackgroundColor = backgroundColor.takeOrElse { DarkGreenGray95.copy(0.7f) }
    placeholder(
        color = specifiedBackgroundColor,
        visible = isLoading,
        shape = shape,
        highlight = highlight
    )
}