package com.example.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.config.AppConfig
import com.example.ui.R
import com.example.ui.components.VerticalSpacer

@Composable
fun AcceptPrivacyDialog(
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
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

                    TermsAndConditions()
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
            imageVector = Icons.Rounded.Lock,
            contentDescription = "Icon",
            modifier = Modifier.padding(12.dp),
            tint = MaterialTheme.colorScheme.onPrimary
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
fun TermsAndConditions() {
    val fullText = stringResource(R.string.terms_and_conditions)
    val tags = listOf(
        mapOf(
            "text" to stringResource(R.string.privacy),
            "tag" to "privacy",
            "url" to AppConfig.PRIVACY_URL
        ),
        mapOf(
            "text" to stringResource(R.string.terms),
            "tag" to "terms",
            "url" to AppConfig.USER_PROTOCOL_URL
        )
    )

    val annotatedString = buildAnnotatedString {
        val defaultStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface)

        append(fullText, defaultStyle)

        tags.forEach { tag ->
            val text = tag["text"] ?: return@forEach
            val url = tag["url"] ?: return@forEach
            val tagText = tag["tag"] ?: return@forEach
            val start = fullText.indexOf(text)
            val end = start + text.length

            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                start = start,
                end = end
            )
            addStringAnnotation(tag = tagText, annotation = url, start = start, end = end)
        }
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(
        style = MaterialTheme.typography.bodyLarge,
        text = annotatedString,
        onClick = { offset ->
            tags.firstNotNullOfOrNull {
                val tag = it["tag"] ?: return@firstNotNullOfOrNull null
                annotatedString.getStringAnnotations(tag, offset, offset).firstOrNull()
            }?.let { string ->
                uriHandler.openUri(string.item)
            }
        }
    )
}

private fun AnnotatedString.Builder.append(text: String, style: SpanStyle) {
    withStyle(style = style) {
        append(text)
    }
}

@Composable
fun ButtonBar(
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { onAcceptRequest() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.accept), style = MaterialTheme.typography.bodyLarge)
        }
        TextButton(
            onClick = { onRejectRequest() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(stringResource(R.string.reject), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
fun AcceptPrivacyPreview() {
    AcceptPrivacyDialog(onAcceptRequest = {}, onRejectRequest = {})
}