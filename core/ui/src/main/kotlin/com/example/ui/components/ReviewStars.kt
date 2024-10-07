package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.composables.composetheme.ComposeTheme
import com.composables.composetheme.amber500
import com.composables.composetheme.buildComposeTheme
import com.composables.composetheme.colors
import com.composables.composetheme.gray100
import kotlin.math.roundToInt

@Composable
fun ReviewStars(value: Float, modifier: Modifier = Modifier, max: Int = 5) {
    Row(
        modifier = modifier.semantics { contentDescription = "$value out of $max rating" },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val fullStars = value.toInt()
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = ComposeTheme.colors.amber500
            )
        }
        if (value - value.toInt() > 0) {
            Box {
                val firstHalf = remember {
                    object : Shape {
                        override fun createOutline(
                            size: Size,
                            layoutDirection: LayoutDirection,
                            density: Density
                        ): Outline {
                            return Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = ComposeTheme.colors.gray100
                )
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    modifier = Modifier.clip(firstHalf),
                    tint = ComposeTheme.colors.amber500
                )
            }
        }
        repeat(max - value.roundToInt()) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = ComposeTheme.colors.gray100
            )
        }
    }
}


@Preview
@Composable
private fun ReviewStarsPreview() {
    val appTheme = buildComposeTheme {}
    appTheme {
        ReviewStars(value = 3.2f)
    }
}