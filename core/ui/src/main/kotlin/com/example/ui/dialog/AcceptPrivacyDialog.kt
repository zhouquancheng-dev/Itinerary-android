package com.example.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.R
import com.example.ui.components.TermsAndConditions
import com.example.ui.components.ThemePreviews
import com.example.ui.components.VerticalSpacer
import com.example.ui.components.resolveColor
import com.example.ui.components.symbols.rememberLock
import com.example.ui.theme.ColorFF576B95
import com.example.ui.theme.ColorFF7D90A9
import com.example.ui.theme.JetItineraryTheme

@Composable
fun AcceptPrivacyDialog(
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
    val fullText = stringResource(R.string.terms_and_conditions)
    val firstTag = stringResource(R.string.privacy)
    val secondTag = stringResource(R.string.terms)

    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier.padding(vertical = 24.dp),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PrivacyIcon()
                    VerticalSpacer(24.dp)

                    PrivacyTitle()
                    VerticalSpacer(12.dp)

                    PrivacyDescription()
                    VerticalSpacer(24.dp)

                    TermsAndConditions(fullText, firstTag, secondTag)
                    VerticalSpacer(48.dp)

                    ButtonBar(onAcceptRequest, onRejectRequest)
                }
            }
        }
    }
}

@Composable
fun PrivacyIcon() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(80.dp)
    ) {
        Icon(
            imageVector = rememberLock(),
            contentDescription = "Icon",
            modifier = Modifier.padding(12.dp),
            tint = Color.White
        )
    }
}

@Composable
fun PrivacyTitle() {
    Text(
        text = stringResource(R.string.privacy_title),
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun PrivacyDescription() {
    Text(
        text = stringResource(R.string.privacy_description),
        textAlign = TextAlign.Start
    )
}

@Composable
fun ButtonBar(
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { onAcceptRequest() },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.app_accept),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        TextButton(
            onClick = { onRejectRequest() },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = resolveColor(ColorFF576B95, ColorFF7D90A9)
            )
        ) {
            Text(
                text = stringResource(R.string.app_reject),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@ThemePreviews
@Composable
private fun AcceptPrivacyPreview() {
    JetItineraryTheme {
        AcceptPrivacyDialog(onAcceptRequest = {}, onRejectRequest = {})
    }
}