package com.example.im.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.PositionalThreshold
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefreshIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.components.LoadingWheel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = PullToRefreshDefaults.containerColor,
    color: Color = PullToRefreshDefaults.indicatorColor,
    threshold: Dp = PositionalThreshold
) {
    Box(
        modifier =
        modifier.pullToRefreshIndicator(
            state = state,
            isRefreshing = isRefreshing,
            containerColor = containerColor,
            threshold = threshold,
        ),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isRefreshing,
            animationSpec = tween(durationMillis = 100), label = ""
        ) { refreshing ->
            if (refreshing) {
                LoadingWheel(contentDesc = "PullRefreshing")
            } else {
                CircularArrowProgressIndicator(
                    progress = { state.distanceFraction },
                    color = color,
                )
            }
        }
    }
}

private val StrokeWidth = 2.5.dp
private val ArcRadius = 5.5.dp
internal val SpinnerSize = 16.dp

private const val MinAlpha = 0.3f
private const val MaxAlpha = 1f
private val AlphaTween = tween<Float>(300, easing = LinearEasing)

private const val MaxProgressArc = 0.8f

private val ArrowWidth = 10.dp
private val ArrowHeight = 5.dp

@Immutable
private class ArrowValues(
    val rotation: Float,
    val startAngle: Float,
    val endAngle: Float,
    val scale: Float
)

private fun ArrowValues(progress: Float): ArrowValues {
    // Discard first 40% of progress. Scale remaining progress to full range between 0 and 100%.
    val adjustedPercent = max(min(1f, progress) - 0.4f, 0f) * 5 / 3
    // How far beyond the threshold pull has gone, as a percentage of the threshold.
    val overshootPercent = abs(progress) - 1.0f
    // Limit the overshoot to 200%. Linear between 0 and 200.
    val linearTension = overshootPercent.coerceIn(0f, 2f)
    // Non-linear tension. Increases with linearTension, but at a decreasing rate.
    val tensionPercent = linearTension - linearTension.pow(2) / 4

    // Calculations based on SwipeRefreshLayout specification.
    val endTrim = adjustedPercent * MaxProgressArc
    val rotation = (-0.25f + 0.4f * adjustedPercent + tensionPercent) * 0.5f
    val startAngle = rotation * 360
    val endAngle = (rotation + endTrim) * 360
    val scale = min(1f, adjustedPercent)

    return ArrowValues(rotation, startAngle, endAngle, scale)
}

private class CircularProgressNode(
    val progress: () -> Float,
    val color: Color,
    val alpha: Float
) : Modifier.Node() {

    private val path = Path().apply { fillType = PathFillType.EvenOdd }

    fun drawCircularArrowProgress(drawScope: DrawScope) {
        val values = ArrowValues(progress())
        drawScope.apply {
            rotate(degrees = values.rotation) {
                val arcRadius = ArcRadius.toPx() + StrokeWidth.toPx() / 2f
                val arcBounds = Rect(center = size.center, radius = arcRadius)
                drawCircularIndicator(color, alpha, values, arcBounds, StrokeWidth)
                drawArrow(path, arcBounds, color, alpha, values, StrokeWidth)
            }
        }
    }

    private fun DrawScope.drawCircularIndicator(
        color: Color,
        alpha: Float,
        values: ArrowValues,
        arcBounds: Rect,
        strokeWidth: Dp
    ) {
        drawArc(
            color = color,
            alpha = alpha,
            startAngle = values.startAngle,
            sweepAngle = values.endAngle - values.startAngle,
            useCenter = false,
            topLeft = arcBounds.topLeft,
            size = arcBounds.size,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
        )
    }

    private fun DrawScope.drawArrow(
        arrow: Path,
        bounds: Rect,
        color: Color,
        alpha: Float,
        values: ArrowValues,
        strokeWidth: Dp,
    ) {
        arrow.reset()
        arrow.moveTo(0f, 0f)
        arrow.lineTo(x = ArrowWidth.toPx() * values.scale / 2, y = ArrowHeight.toPx() * values.scale)
        arrow.lineTo(x = ArrowWidth.toPx() * values.scale, y = 0f)

        val radius = min(bounds.width, bounds.height) / 2f
        val inset = ArrowWidth.toPx() * values.scale / 2f
        arrow.translate(Offset(x = radius + bounds.center.x - inset, y = bounds.center.y - strokeWidth.toPx()))

        rotate(degrees = values.endAngle - strokeWidth.toPx()) {
            drawPath(path = arrow, color = color, alpha = alpha, style = Stroke(strokeWidth.toPx()))
        }
    }
}

@Composable
private fun CircularArrowProgressIndicator(
    progress: () -> Float,
    color: Color,
) {
    // Calculate target alpha based on progress
    val targetAlpha by remember { derivedStateOf { if (progress() >= 1f) MaxAlpha else MinAlpha } }
    // Animate the alpha value
    val alphaState by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = AlphaTween,
        label = ""
    )

    Spacer(
        Modifier
            .size(SpinnerSize)
            .circularProgressModifier(progress, color, alphaState)
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(progress(), 0f..1f, 0)
            }
    )
}

private fun Modifier.circularProgressModifier(
    progress: () -> Float,
    color: Color,
    alpha: Float
) = this.then(
    object : DrawModifier {
        override fun ContentDrawScope.draw() {
            val node = CircularProgressNode(progress, color, alpha)
            node.drawCircularArrowProgress(this)
        }
    }
)
