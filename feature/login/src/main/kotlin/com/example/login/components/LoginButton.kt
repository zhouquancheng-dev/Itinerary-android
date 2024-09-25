package com.example.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.login.R
import com.example.login.state.isValidPhoneNumber
import com.example.ui.theme.JetItineraryTheme

@Composable
fun LoginButton(
    phoneNumber: String,
    getting: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = { onClick() },
        enabled = isValidPhoneNumber(phoneNumber) && phoneNumber.length == 11,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.inversePrimary
        )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (centerItem, rightItem) = createRefs()

            Text(
                text = stringResource(R.string.send_sms_code),
                modifier = Modifier.constrainAs(centerItem) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                fontSize = 20.sp
            )

            if (getting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .constrainAs(rightItem) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.surface,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginButtonPreview() {
    JetItineraryTheme {
        LoginButton(
            phoneNumber = "13620221824",
            getting = false,
            onClick = {}
        )
    }
}