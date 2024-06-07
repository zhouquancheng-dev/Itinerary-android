package com.example.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.component.PasswordTextField
import com.example.login.component.PhoneNumberTextField
import com.example.login.component.SmsCodeTextField
import com.example.login.component.UserTopAppBar
import com.example.login.obj.ConfirmPasswordState
import com.example.login.obj.PasswordState
import com.example.login.obj.PhoneNumberVisualTransformation
import com.example.login.obj.formatPhoneNumber
import com.example.login.obj.isValidPhoneNumber
import com.example.ui.theme.JetItineraryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun RegisterPage(
    onBackClick: () -> Unit,
    onSendClick: () -> Unit,
    onRegisterClick: (username: String, code: String, password: String, confirmPassword: String) -> Unit
) {
    var remainingTime by remember { mutableIntStateOf(90) }
    var isCountingDown by remember { mutableStateOf(false) }

    var phoneNumberText by remember { mutableStateOf("") }
    val phoneNumberVisualTransformation = remember(phoneNumberText) {
        PhoneNumberVisualTransformation(phoneNumberText)
    }
    var codeText by remember { mutableStateOf("") }
    val passwordState = remember { PasswordState() }
    val confirmPasswordState = remember { ConfirmPasswordState(passwordState) }

    val scope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()

    // 本地软件键盘控制器
    val keyboardActions = LocalSoftwareKeyboardController.current
    // 焦点管理器
    val focusStateManger = LocalFocusManager.current

    val textFieldModifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .padding(horizontal = 30.dp)

    val countdownFlow: Flow<Int> = flow {
        for (index in 0..90) {
            emit(90 - index)
            delay(1000)
        }
    }
    LaunchedEffect(isCountingDown) {
        if (isCountingDown) {
            scope.launch {
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
                title = stringResource(R.string.register_topBar_text)
            ) {
                onBackClick()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.welcome_text),
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(R.string.createAccount_text),
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Column(
                modifier = Modifier.height(350.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                PhoneNumberTextField(
                    phoneNumberText = phoneNumberText,
                    onPhoneNumberChange = { newPhoneNumber ->
                        phoneNumberText = formatPhoneNumber(newPhoneNumber)
                    },
                    modifier = textFieldModifier,
                    shape = RoundedCornerShape(12.dp),
                    phoneNumberVisualTransformation = phoneNumberVisualTransformation,
                    onCloseClick = {
                        phoneNumberText = ""
                    }
                )
                if (isValidPhoneNumber(phoneNumberText) && phoneNumberText.length == 11) {
                    Text(
                        text = stringResource(R.string.phoneNumberError_text),
                        modifier = Modifier.padding(start = 35.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                SmsCodeTextField(
                    codeText = codeText,
                    onValueChange = { newCodeText ->
                        if (newCodeText.all { it.isDigit() }) {
                            codeText = newCodeText
                        }
                    },
                    isCountingDown = isCountingDown,
                    remainingTime = remainingTime,
                    modifier = textFieldModifier,
                    enabled = !isCountingDown && !isValidPhoneNumber(phoneNumberText) && phoneNumberText.length == 11,
                    shape = RoundedCornerShape(12.dp),
                    onSendClick = {
                        if (!isCountingDown) {
                            isCountingDown = true
                            onSendClick()
                        }
                    }
                )

                PasswordTextField(
                    label = stringResource(R.string.pwd_text),
                    passwordState = passwordState,
                    modifier = textFieldModifier,
                    shape = RoundedCornerShape(12.dp),
                    imeAction = ImeAction.Next
                )

                PasswordTextField(
                    label = stringResource(R.string.confirmPwd_text),
                    passwordState = confirmPasswordState,
                    modifier = textFieldModifier,
                    shape = RoundedCornerShape(12.dp),
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        keyboardActions?.hide()
                        focusStateManger.clearFocus()
                    }
                )

                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip(
                            contentColor = MaterialTheme.colorScheme.inverseSurface,
                            containerColor = MaterialTheme.colorScheme.inverseOnSurface
                        ) {
                            Column {
                                Text(stringArrayResource(R.array.pwdStrength_Help_arrayText)[0])
                                Text(stringArrayResource(R.array.pwdStrength_Help_arrayText)[1])
                                Text(stringArrayResource(R.array.pwdStrength_Help_arrayText)[2])
                                Text(stringArrayResource(R.array.pwdStrength_Help_arrayText)[3])
                            }
                        }
                    },
                    state = tooltipState,
                    modifier = Modifier.padding(start = 30.dp, top = 8.dp)
                ) {
                    ElevatedButton(
                        onClick = {
                            scope.launch {
                                tooltipState.show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = null
                        )
                        Text(text = stringResource(R.string.pwdStrength_text))
                    }
                }
            }

            Spacer(modifier = Modifier.height(70.dp))

            FilledTonalButton(
                onClick = {
                    onRegisterClick(phoneNumberText, codeText, passwordState.text, confirmPasswordState.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 35.dp),
                enabled = isValid(phoneNumberText, codeText, passwordState, confirmPasswordState)
            ) {
                Text(
                    text = stringResource(R.string.register_topBar_text),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun isValid(
    phoneNumberText: String,
    codeText: String,
    passwordState: PasswordState,
    confirmPasswordState: ConfirmPasswordState
): Boolean {
    return (!isValidPhoneNumber(phoneNumberText) && phoneNumberText.length == 11)
            && (codeText.isNotBlank())
            && (passwordState.isValid && confirmPasswordState.isValid)
}

@ExperimentalMaterial3Api
@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
fun RegisterPagePreview() {
    JetItineraryTheme {
        RegisterPage(
            onBackClick = {},
            onSendClick = {},
            onRegisterClick = { _, _, _, _ -> }
        )
    }
}