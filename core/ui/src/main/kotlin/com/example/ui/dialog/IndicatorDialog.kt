package com.example.ui.dialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.common.util.isDarkMode
import com.example.ui.R
import com.example.ui.theme.JetItineraryTheme

@Composable
fun IndicatorDialog(
    showDialog: Boolean,
    dialogText: String
) {
    val context = LocalContext.current
    val dialogBgColor =
        if (context.isDarkMode()) colorResource(R.color.example_gray)
        else colorResource(R.color.black_transparent)

    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier.size(125.dp),
                shape = RoundedCornerShape(15.dp),
                color = dialogBgColor
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        trackColor = Color.Transparent,
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = dialogText,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressDialog(
    showDialog: Boolean,
    progress: Float,
    dialogText: String
) {
    val context = LocalContext.current
    val dialogBgColor =
        if (context.isDarkMode()) colorResource(R.color.example_gray)
        else colorResource(R.color.black_transparent)

    val normalizedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = normalizedProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "progress"
    )

    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier.size(125.dp),
                shape = RoundedCornerShape(15.dp),
                color = dialogBgColor
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(45.dp),
                            color = Color.White,
                            trackColor = Color.Transparent,
                            strokeWidth = 3.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = dialogText,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun IndicatorDialogPreview() {
    JetItineraryTheme {
        val context = LocalContext.current
        val dialogBgColor =
            if (context.isDarkMode()) colorResource(R.color.example_gray)
            else colorResource(R.color.black_transparent)

        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier.size(125.dp),
                shape = RoundedCornerShape(15.dp),
                color = dialogBgColor
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                ) {
                    CircularProgressIndicator(
                        progress = { 0.6f },
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        trackColor = Color.Transparent,
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "加载中",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ProgressDialogPreview() {
    JetItineraryTheme {
        val context = LocalContext.current
        val dialogBgColor =
            if (context.isDarkMode()) colorResource(R.color.example_gray)
            else colorResource(R.color.black_transparent)

        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier.size(125.dp),
                shape = RoundedCornerShape(15.dp),
                color = dialogBgColor
            ) {
                Column(
                    modifier = Modifier.wrapContentSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(45.dp),
                            color = Color.White,
                            trackColor = Color.Transparent,
                            strokeWidth = 3.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "100%",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = "上传中",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}