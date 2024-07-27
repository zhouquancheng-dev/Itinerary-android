package com.example.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.R
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.view.isDarkMode

@Composable
fun ProgressIndicatorDialog(
    showDialog: Boolean,
    dialogText: String
) {
    val context = LocalContext.current
    val dialogBgColor =
        if (context.isDarkMode()) colorResource(R.color.white)
        else colorResource(R.color.black_transparent)

    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier.size(145.dp),
                shape = RoundedCornerShape(15.dp),
                color = dialogBgColor
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.background,
                        trackColor = MaterialTheme.colorScheme.onSurface,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = dialogText,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro")
@PreviewLightDark
@Composable
private fun ProgressIndicatorDialogPreview() {
    JetItineraryTheme {
        ProgressIndicatorDialog(true, "正在加载")
    }
}