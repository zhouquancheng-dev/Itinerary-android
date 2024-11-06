package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.common.config.AppConfig
import com.example.ui.theme.ColorFF6195F9
import com.example.ui.theme.ColorFFFA5151
import com.example.ui.theme.JetItineraryTheme

@Composable
fun TermsAndConditions(
    fullText: String,
    firstTag: String,
    secondTag: String
) {
    val linkStyle = TextLinkStyles(
        style = SpanStyle(color = ColorFF6195F9, fontWeight = FontWeight.SemiBold),
        focusedStyle = null,
        hoveredStyle = null,
        pressedStyle = SpanStyle(color = ColorFFFA5151)
    )

    val annotatedString = buildAnnotatedString {
        val defaultStyle = SpanStyle(color = MaterialTheme.colorScheme.onBackground)
        append(fullText, defaultStyle)

        val firstTagStart = fullText.indexOf(firstTag)
        val firstTagEnd = firstTagStart + firstTag.length
        addLink(
            LinkAnnotation.Url(
                url = AppConfig.PRIVACY_URL,
                styles = linkStyle,
                linkInteractionListener = {

                }
            ),
            start = firstTagStart,
            end = firstTagEnd
        )

        val secondTagStart = fullText.indexOf(secondTag)
        val secondTagEnd = secondTagStart + secondTag.length
        addLink(
            LinkAnnotation.Url(
                url = AppConfig.USER_PROTOCOL_URL,
                styles = linkStyle,
                linkInteractionListener = {

                }
            ),
            start = secondTagStart,
            end = secondTagEnd
        )
    }

    BasicText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
    )
}

private fun AnnotatedString.Builder.append(text: String, style: SpanStyle) {
    withStyle(style = style) {
        append(text)
    }
}

@PreviewLightDark
@Composable
private fun TermsAndConditionsPreview() {
    JetItineraryTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            TermsAndConditions(
                fullText = "注册即表示您同意我们的用户协议以及隐私政策。",
                firstTag = "用户协议",
                secondTag = "隐私政策"
            )
        }
    }
}