package com.example.ui.components.symbols

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

@Composable
fun rememberVerticalAlignTop(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "vertical_align_top",
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
                moveTo(8.167f, 7.833f)
                quadToRelative(-0.542f, 0f, -0.938f, -0.395f)
                quadToRelative(-0.396f, -0.396f, -0.396f, -0.938f)
                quadToRelative(0f, -0.542f, 0.396f, -0.917f)
                reflectiveQuadToRelative(0.938f, -0.375f)
                horizontalLineToRelative(23.666f)
                quadToRelative(0.542f, 0f, 0.938f, 0.375f)
                quadToRelative(0.396f, 0.375f, 0.396f, 0.917f)
                quadToRelative(0f, 0.583f, -0.396f, 0.958f)
                reflectiveQuadToRelative(-0.938f, 0.375f)
                close()
                moveTo(20f, 34.708f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.916f)
                verticalLineTo(15.5f)
                lineToRelative(-3.875f, 3.833f)
                quadToRelative(-0.375f, 0.375f, -0.895f, 0.375f)
                quadToRelative(-0.521f, 0f, -0.896f, -0.416f)
                quadToRelative(-0.417f, -0.375f, -0.417f, -0.917f)
                reflectiveQuadToRelative(0.417f, -0.917f)
                lineToRelative(6.041f, -6.083f)
                quadToRelative(0.209f, -0.208f, 0.438f, -0.292f)
                quadTo(19.75f, 11f, 20f, 11f)
                quadToRelative(0.25f, 0f, 0.479f, 0.083f)
                quadToRelative(0.229f, 0.084f, 0.438f, 0.292f)
                lineTo(27f, 17.458f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.917f)
                reflectiveQuadToRelative(-0.417f, 0.917f)
                quadToRelative(-0.375f, 0.375f, -0.916f, 0.375f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                lineTo(21.333f, 15.5f)
                verticalLineToRelative(17.917f)
                quadToRelative(0f, 0.541f, -0.395f, 0.916f)
                quadToRelative(-0.396f, 0.375f, -0.938f, 0.375f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberDelete(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "delete",
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
                moveTo(11.208f, 34.708f)
                quadToRelative(-1.041f, 0f, -1.833f, -0.77f)
                quadToRelative(-0.792f, -0.771f, -0.792f, -1.855f)
                verticalLineTo(9.25f)
                horizontalLineTo(8.25f)
                quadToRelative(-0.583f, 0f, -0.958f, -0.396f)
                reflectiveQuadToRelative(-0.375f, -0.937f)
                quadToRelative(0f, -0.542f, 0.375f, -0.917f)
                reflectiveQuadToRelative(0.958f, -0.375f)
                horizontalLineToRelative(6.458f)
                quadToRelative(0f, -0.583f, 0.375f, -0.958f)
                reflectiveQuadToRelative(0.959f, -0.375f)
                horizontalLineTo(24f)
                quadToRelative(0.583f, 0f, 0.938f, 0.375f)
                quadToRelative(0.354f, 0.375f, 0.354f, 0.958f)
                horizontalLineToRelative(6.5f)
                quadToRelative(0.541f, 0f, 0.937f, 0.375f)
                reflectiveQuadToRelative(0.396f, 0.917f)
                quadToRelative(0f, 0.583f, -0.396f, 0.958f)
                reflectiveQuadToRelative(-0.937f, 0.375f)
                horizontalLineToRelative(-0.334f)
                verticalLineToRelative(22.833f)
                quadToRelative(0f, 1.084f, -0.791f, 1.855f)
                quadToRelative(-0.792f, 0.77f, -1.875f, 0.77f)
                close()
                moveToRelative(0f, -25.458f)
                verticalLineToRelative(22.833f)
                horizontalLineToRelative(17.584f)
                verticalLineTo(9.25f)
                close()
                moveToRelative(4.125f, 18.042f)
                quadToRelative(0f, 0.583f, 0.396f, 0.958f)
                reflectiveQuadToRelative(0.938f, 0.375f)
                quadToRelative(0.541f, 0f, 0.916f, -0.375f)
                reflectiveQuadToRelative(0.375f, -0.958f)
                verticalLineTo(14f)
                quadToRelative(0f, -0.542f, -0.375f, -0.937f)
                quadToRelative(-0.375f, -0.396f, -0.916f, -0.396f)
                quadToRelative(-0.584f, 0f, -0.959f, 0.396f)
                quadToRelative(-0.375f, 0.395f, -0.375f, 0.937f)
                close()
                moveToRelative(6.709f, 0f)
                quadToRelative(0f, 0.583f, 0.396f, 0.958f)
                quadToRelative(0.395f, 0.375f, 0.937f, 0.375f)
                reflectiveQuadToRelative(0.937f, -0.375f)
                quadToRelative(0.396f, -0.375f, 0.396f, -0.958f)
                verticalLineTo(14f)
                quadToRelative(0f, -0.542f, -0.396f, -0.937f)
                quadToRelative(-0.395f, -0.396f, -0.937f, -0.396f)
                reflectiveQuadToRelative(-0.937f, 0.396f)
                quadToRelative(-0.396f, 0.395f, -0.396f, 0.937f)
                close()
                moveTo(11.208f, 9.25f)
                verticalLineToRelative(22.833f)
                verticalLineTo(9.25f)
                close()
            }
        }.build()
    }
}