package com.example.login.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.state.VerifyCodeState
import com.example.login.theme.ColorFF6195F9
import com.example.ui.components.ThemePreviews
import com.example.ui.theme.JetItineraryTheme
import kotlinx.coroutines.delay

@Composable
fun SeparateVerificationCodeTextField(
    codeValue: String,
    codeTextLength: Int,
    onValueChange: (String) -> Unit
) {
    BasicVerificationCodeTextField(
        codeText = codeValue,
        onValueChange = onValueChange,
        codeTextLength = codeTextLength
    ) { codeLength, codeIndex, code ->
        val textSize = remember { (300.dp.value / codeLength / 2).sp }

        val isCode = codeIndex < code.length
        val verificationCodeState by remember(codeIndex, code.length) {
            derivedStateOf {
                when {
                    isCode -> VerifyCodeState.ENTERED
                    codeIndex == code.length -> VerifyCodeState.INPUTTING
                    else -> VerifyCodeState.PENDING
                }
            }
        }

        val contentColor = remember(verificationCodeState) {
            when (verificationCodeState) {
                VerifyCodeState.ENTERED, VerifyCodeState.INPUTTING -> Color.Black
                VerifyCodeState.PENDING -> Color.Transparent
            }
        }

        val border = remember(verificationCodeState) {
            when (verificationCodeState) {
                VerifyCodeState.ENTERED, VerifyCodeState.PENDING -> null
                VerifyCodeState.INPUTTING -> BorderStroke(width = 3.dp, color = ColorFF6195F9)
            }
        }

        val elevation = remember(verificationCodeState) {
            when (verificationCodeState) {
                VerifyCodeState.ENTERED -> 1.dp
                VerifyCodeState.INPUTTING -> 0.dp
                VerifyCodeState.PENDING -> 1.dp
            }
        }

        key(elevation) {
            Surface (
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(3.dp),
                color = colorResource(R.color.text_field_bg),
                contentColor = contentColor,
                shape = RoundedCornerShape(10.dp),
                border = border,
                tonalElevation = elevation,
                shadowElevation = elevation
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when (verificationCodeState) {
                        VerifyCodeState.ENTERED -> {
                            Text(
                                text = codeValue[codeIndex].toString(),
                                fontSize = textSize,
                                textAlign = TextAlign.Center
                            )
                        }

                        VerifyCodeState.INPUTTING -> {
                            var isCursorVisible by remember { mutableStateOf(true) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    isCursorVisible = !isCursorVisible
                                    delay(600)
                                }
                            }
                            if (isCursorVisible) {
                                Text(
                                    text = "|",
                                    fontSize = textSize,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        VerifyCodeState.PENDING -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun BasicVerificationCodeTextField(
    codeText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    codeTextLength: Int,
    verificationCodeBox: @Composable RowScope.(codeLength: Int, codeIndex: Int, codeText: String) -> Unit
) {
    BasicTextField(
        value = codeText,
        onValueChange = onValueChange,
        readOnly = true,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
        ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                for (index in 0 until codeTextLength) {
                    verificationCodeBox(codeTextLength, index, codeText)
                }
            }
        }
    )
}

@ThemePreviews
@Composable
private fun SeparateVerificationCodeTextFieldPreview() {
    JetItineraryTheme {
        SeparateVerificationCodeTextField(
            codeValue = "1",
            codeTextLength = 6,
            onValueChange = {}
        )
    }
}