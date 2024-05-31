package com.example.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.example.common.util.ClickUtils.isFastClick

inline fun Modifier.click(
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    rippleColor: Color = Color.Unspecified,
    enableClickDebounce: Boolean = false,
    crossinline onClick: () -> Unit
) = composed {
    this.then(
        clickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = if (showRipple) {
                rememberRipple(
                    bounded = bounded,
                    radius = radius,
                    color = rippleColor
                )
            } else null,
            onClickLabel = onClickLabel,
            role = role
        ) {
            if (!enableClickDebounce || !isFastClick()) {
                onClick()
            }
        }
    )
}

/**
 * 自定义扩展 Modifier.clickable()
 * [showRipple]根据需要是否需要涟漪效果
 */
inline fun Modifier.clickable(
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    this.then(
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = if (showRipple) LocalIndication.current else null,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role
        ) {
            onClick()
        }
    )
}

/**
 * 自定义扩展 Modifier.clickable()
 * 无涟漪动画效果的单击点击事件
 * @param onClick 当用户点击元素时将被调用
 */
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    this.then(
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick()
        }
    )
}