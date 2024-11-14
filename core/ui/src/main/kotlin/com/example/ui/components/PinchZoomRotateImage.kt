package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.lerp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.rememberAsyncImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PinchZoomRotateImage(
    imageModel: Any,
    modifier: Modifier = Modifier
) {
    val imagePainter = rememberAsyncImagePainter(model = imageModel)

    val maxScale = 5f // 最大缩放比例
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // 图片尺寸
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    // 容器尺寸
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        // 平滑缩放效果
        val targetScale = (scale * zoomChange).coerceIn(1f, maxScale)
        scale = lerp(scale, targetScale, 1f)

        // 平滑的平移效果，加入缩放影响的调整系数
        val adjustedOffsetChange = panChange * scale * 1.5f
        val tempOffset = offset + adjustedOffsetChange

        val scaledImageWidth = imageSize.width * scale
        val scaledImageHeight = imageSize.height * scale

        val maxX = (scaledImageWidth - containerSize.width).coerceAtLeast(0f) / 2
        val maxY = (scaledImageHeight - containerSize.height).coerceAtLeast(0f) / 2

        offset = Offset(
            x = lerp(offset.x, tempOffset.x.coerceIn(-maxX, maxX), 1f),
            y = lerp(offset.y, tempOffset.y.coerceIn(-maxY, maxY), 1f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                containerSize = layoutCoordinates.size
            }
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .onSizeChanged { newSize ->
                    imageSize = newSize
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state)
        )
    }
}
