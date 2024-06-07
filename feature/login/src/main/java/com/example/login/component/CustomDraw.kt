package com.example.login.component

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath

/**
 * 验证码校验页面顶部背景路径裁剪
 */
fun DrawScope.drawVerificationCodePageBackground(
    path: Path,
    brush: Brush
) {
    val canvasWidth = size.width
    val canvasHeight = size.height

    path.moveTo(0f, 0f)
    path.lineTo(0f, canvasHeight / 5f)
    path.cubicTo(
        x1 = canvasWidth / 2.5f,
        y1 = canvasHeight / 5.5f,
        x2 = canvasWidth / 2f,
        y2 = canvasHeight / 2.5f,
        x3 = size.width,
        y3 = canvasHeight / 5f
    )
    path.lineTo(size.width, 0f)
    path.close()
    clipPath(path) {
        drawRect(brush = brush)
    }
}