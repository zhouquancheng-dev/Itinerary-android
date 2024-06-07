package com.example.login

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.component.SeparateVerificationCodeTextField
import com.example.login.component.UserTopAppBar
import com.example.login.component.drawVerificationCodePageBackground
import com.example.ui.theme.BackgroundColorsBrush
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.MyLightBlueTextColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * 验证码校验页
 * @param phoneNumber 手机号
 * @param onBackClick 返回
 * @param onSendClick 发送验证码
 * @param onVerifyClick 发起校验请求
 */
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@ExperimentalComposeUiApi
@Composable
fun VerifyCodePage(
    phoneNumber: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSendClick: () -> Unit,
    onVerifyClick: (String) -> Unit
) {
    var codeText by remember { mutableStateOf("") }

    var remainingTime by remember { mutableIntStateOf(90) }
    var isCountingDown by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val insets = WindowInsets.isImeVisible
    val keyboardActions = LocalSoftwareKeyboardController.current

    val countdownFlow: Flow<Int> = flow {
        for (index in 0..90) {
            emit(90 - index)
            delay(1000)
        }
    }

    LaunchedEffect(isCountingDown) {
        if (isCountingDown) {
            onSendClick()
            coroutineScope.launch {
                countdownFlow.collect { newRemainingTime ->
                    remainingTime = newRemainingTime
                    if (newRemainingTime == 0) {
                        isCountingDown = false
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            UserTopAppBar(
                title = stringResource(R.string.verify_text),
                titleColor = Color.Black,
                iconTint = Color.Black,
                topAppBarColors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFC0D3FF))
            ) {
                if (insets) {
                    keyboardActions?.hide()
                }
                onBackClick()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .drawWithCache {
                    val path = Path()
                    val brush = Brush.verticalGradient(BackgroundColorsBrush)
                    onDrawBehind {
                        drawVerificationCodePageBackground(path, brush)
                    }
                }
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.bg_phone_verify),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = stringResource(R.string.sendSmsCodeTo_text), fontSize = 18.sp)
            Text(text = phoneNumber, fontSize = 16.sp, modifier = Modifier.padding(top = 10.dp))

            Spacer(modifier = Modifier.height(10.dp))

            SeparateVerificationCodeTextField(
                codeValue = codeText,
                onValueChange = { newText ->
                    if (newText.length <= 4 && newText.all { it.isDigit() }) {
                        codeText = newText
                        if (newText.length == 4) {
                            onVerifyClick(newText)
                            codeText = ""
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FilledTonalButton(
                onClick = {
                    if (!isCountingDown) {
                        isCountingDown = true
                    }
                },
                modifier = Modifier.width(200.dp).height(55.dp),
                enabled = !isCountingDown
            ) {
                val annotatedString = buildAnnotatedString {
                    if (isCountingDown) {
                        withStyle(style = SpanStyle(fontSize = 18.sp, color = MyLightBlueTextColor)) {
                            append("${remainingTime}s")
                        }
                        withStyle(style = SpanStyle(fontSize = 18.sp)) {
                            append(stringResource(R.string.afterResend_text))
                        }
                    } else {
                        withStyle(style = SpanStyle(fontSize = 18.sp)) {
                            append(stringResource(R.string.resend_text))
                        }
                    }
                }

                Text(text = annotatedString)
            }
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@ExperimentalComposeUiApi
@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
fun VerifyCodePagePreview() {
    JetItineraryTheme {
        VerifyCodePage(
            phoneNumber = "13620221824",
            onBackClick = {},
            onSendClick = {},
            onVerifyClick = { _ -> }
        )
    }
}