package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberKeyboardBackspace(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "keyboard_backspace",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14.083f, 28.792f)
                lineToRelative(-7.958f, -7.917f)
                quadToRelative(-0.208f, -0.208f, -0.292f, -0.417f)
                quadToRelative(-0.083f, -0.208f, -0.083f, -0.5f)
                quadToRelative(0f, -0.25f, 0.083f, -0.479f)
                quadToRelative(0.084f, -0.229f, 0.292f, -0.437f)
                lineToRelative(7.958f, -7.959f)
                quadToRelative(0.375f, -0.375f, 0.938f, -0.375f)
                quadToRelative(0.562f, 0f, 0.937f, 0.375f)
                quadToRelative(0.375f, 0.417f, 0.375f, 0.959f)
                quadToRelative(0f, 0.541f, -0.375f, 0.958f)
                lineToRelative(-5.666f, 5.667f)
                horizontalLineToRelative(23.166f)
                quadToRelative(0.584f, 0f, 0.959f, 0.375f)
                reflectiveQuadToRelative(0.375f, 0.916f)
                quadToRelative(0f, 0.584f, -0.375f, 0.959f)
                reflectiveQuadToRelative(-0.959f, 0.375f)
                horizontalLineTo(10.292f)
                lineToRelative(5.666f, 5.666f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.917f)
                reflectiveQuadToRelative(-0.375f, 0.917f)
                quadToRelative(-0.416f, 0.416f, -0.937f, 0.416f)
                quadToRelative(-0.521f, 0f, -0.938f, -0.416f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberClose(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "close",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 21.875f)
                lineToRelative(-8.5f, 8.5f)
                quadToRelative(-0.417f, 0.375f, -0.938f, 0.375f)
                quadToRelative(-0.52f, 0f, -0.937f, -0.375f)
                quadToRelative(-0.375f, -0.417f, -0.375f, -0.937f)
                quadToRelative(0f, -0.521f, 0.375f, -0.938f)
                lineToRelative(8.542f, -8.542f)
                lineToRelative(-8.542f, -8.5f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.916f)
                quadToRelative(0f, -0.542f, 0.375f, -0.917f)
                quadToRelative(0.417f, -0.417f, 0.937f, -0.417f)
                quadToRelative(0.521f, 0f, 0.938f, 0.417f)
                lineToRelative(8.5f, 8.5f)
                lineToRelative(8.5f, -8.5f)
                quadToRelative(0.417f, -0.375f, 0.938f, -0.375f)
                quadToRelative(0.52f, 0f, 0.937f, 0.375f)
                quadToRelative(0.375f, 0.417f, 0.375f, 0.938f)
                quadToRelative(0f, 0.52f, -0.375f, 0.937f)
                lineTo(21.833f, 20f)
                lineToRelative(8.542f, 8.542f)
                quadToRelative(0.375f, 0.375f, 0.396f, 0.916f)
                quadToRelative(0.021f, 0.542f, -0.396f, 0.917f)
                quadToRelative(-0.375f, 0.375f, -0.917f, 0.375f)
                quadToRelative(-0.541f, 0f, -0.916f, -0.375f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberLock(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "lock",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9.542f, 36.375f)
                quadToRelative(-1.084f, 0f, -1.855f, -0.771f)
                quadToRelative(-0.77f, -0.771f, -0.77f, -1.854f)
                verticalLineTo(16.292f)
                quadToRelative(0f, -1.084f, 0.77f, -1.854f)
                quadToRelative(0.771f, -0.771f, 1.855f, -0.771f)
                horizontalLineToRelative(2.583f)
                verticalLineTo(9.958f)
                quadToRelative(0f, -3.291f, 2.292f, -5.583f)
                quadTo(16.708f, 2.083f, 20f, 2.083f)
                quadToRelative(3.292f, 0f, 5.583f, 2.292f)
                quadToRelative(2.292f, 2.292f, 2.292f, 5.583f)
                verticalLineToRelative(3.709f)
                horizontalLineToRelative(2.583f)
                quadToRelative(1.084f, 0f, 1.854f, 0.771f)
                quadToRelative(0.771f, 0.77f, 0.771f, 1.854f)
                verticalLineTo(33.75f)
                quadToRelative(0f, 1.083f, -0.771f, 1.854f)
                quadToRelative(-0.77f, 0.771f, -1.854f, 0.771f)
                close()
                moveToRelative(5.25f, -22.708f)
                horizontalLineToRelative(10.416f)
                verticalLineToRelative(-3.75f)
                quadToRelative(0f, -2.167f, -1.5f, -3.688f)
                quadToRelative(-1.5f, -1.521f, -3.708f, -1.521f)
                quadToRelative(-2.167f, 0f, -3.688f, 1.521f)
                quadToRelative(-1.52f, 1.521f, -1.52f, 3.729f)
                close()
                moveTo(20f, 28.208f)
                quadToRelative(1.292f, 0f, 2.229f, -0.916f)
                quadToRelative(0.938f, -0.917f, 0.938f, -2.209f)
                quadToRelative(0f, -1.25f, -0.938f, -2.229f)
                quadToRelative(-0.937f, -0.979f, -2.229f, -0.979f)
                reflectiveQuadToRelative(-2.229f, 0.979f)
                quadToRelative(-0.938f, 0.979f, -0.938f, 2.229f)
                quadToRelative(0f, 1.292f, 0.938f, 2.209f)
                quadToRelative(0.937f, 0.916f, 2.229f, 0.916f)
                close()
            }
        }.build()
    }
}