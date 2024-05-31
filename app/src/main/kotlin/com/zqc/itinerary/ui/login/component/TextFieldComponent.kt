package com.zqc.itinerary.ui.login.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Textsms
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zqc.itinerary.R
import com.zqc.itinerary.ui.login.obj.PasswordState
import com.zqc.itinerary.ui.login.obj.PhoneNumberVisualTransformation
import com.zqc.itinerary.ui.login.obj.TextFieldState
import com.zqc.itinerary.ui.login.obj.UserState
import com.zqc.itinerary.ui.login.obj.VerifyCodeState
import com.zqc.itinerary.ui.login.obj.isValidPhoneNumber
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.VerifyCodeEntered
import com.example.ui.theme.VerifyCodePending
import com.example.ui.theme.MyLightBlueTextColor
import kotlinx.coroutines.delay

/**
 * 账号输入框
 */
@Composable
fun Account(
    label: String,
    userState: TextFieldState,
    modifier: Modifier = Modifier,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = userState.text,
        onValueChange = { userState.text = it },
        modifier = modifier,
        shape = shape,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onImeAction() })
    )
}

/**
 * 密码输入框
 */
@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    // 密文状态，默认不明文显示密码字符
    val showPasswordVisual = remember { mutableStateOf(false) }

    val keyboardActions = LocalSoftwareKeyboardController.current
    val focusStateManger = LocalFocusManager.current

    OutlinedTextField(
        value = passwordState.text,
        onValueChange = { passwordState.text = it },
        modifier = modifier,
        shape = shape,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            val lock = if (showPasswordVisual.value) {
                Icons.Outlined.LockOpen
            } else {
                Icons.Outlined.Lock
            }
            Icon(imageVector = lock, contentDescription = null)
        },
        trailingIcon = {
            if (showPasswordVisual.value) {
                IconButton(onClick = { showPasswordVisual.value = false }) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "hide password"
                    )
                }
            } else {
                IconButton(onClick = { showPasswordVisual.value = true }) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = "show password"
                    )
                }
            }
        },
        visualTransformation = if (showPasswordVisual.value) VisualTransformation.None else
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardActions?.hide()
                focusStateManger.clearFocus()
                onImeAction()
            }
        )
    )
}

@Composable
fun PasswordTextField(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    suffix: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    // 密文状态，默认不明文显示密码字符
    val showPasswordVisual = remember { mutableStateOf(false) }

    TextField(
        value = passwordState.text,
        onValueChange = { newValue ->
            passwordState.text = newValue
        },
        modifier = modifier,
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Password,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(25.dp)
            )
        },
        trailingIcon = {
            if (showPasswordVisual.value) {
                IconButton(onClick = { showPasswordVisual.value = false }) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "hide password"
                    )
                }
            } else {
                IconButton(onClick = { showPasswordVisual.value = true }) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = "show password"
                    )
                }
            }
        },
        suffix = suffix,
        singleLine = true,
        shape = shape,
        visualTransformation = if (showPasswordVisual.value) VisualTransformation.None else
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

/**
 * 手机号输入框 OutlinedTextField
 */
@Composable
fun PhoneNumberOutlinedTextField(
    phoneNumberText: String,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefixIcon: ImageVector = Icons.Rounded.Phone,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    phoneNumberVisualTransformation: PhoneNumberVisualTransformation,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusStateManger = LocalFocusManager.current

    OutlinedTextField(
        value = phoneNumberText,
        onValueChange = { newPhoneNumber ->
            if (newPhoneNumber.length <= 11 && newPhoneNumber.all { it.isDigit() }) {
                onPhoneNumberChange(newPhoneNumber)
                if (newPhoneNumber.length == 11) {
                    focusStateManger.clearFocus()
                }
            }
        },
        modifier = modifier,
        shape = shape,
        singleLine = true,
        isError = isValidPhoneNumber(phoneNumberText) && phoneNumberText.length == 11,
        textStyle = LocalTextStyle.current.copy(fontSize = 21.sp, fontWeight = FontWeight.Bold),
        prefix = {
            Icon(
                imageVector = prefixIcon,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(25.dp)
            )
        },
        trailingIcon = {
            if (phoneNumberText.isNotEmpty()) {
                IconButton(
                    onClick = { onCloseClick() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
            }
        },
        visualTransformation = phoneNumberVisualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusStateManger.clearFocus()
                keyboardController?.hide()
                onImeAction()
            }
        )
    )
}

/**
 * 手机号输入框 TextField
 */
@Composable
fun PhoneNumberTextField(
    phoneNumberText: String,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    phoneNumberVisualTransformation: PhoneNumberVisualTransformation,
    imeAction: ImeAction = ImeAction.Next,
    onCloseClick: () -> Unit = {}
) {
    TextField(
        value = phoneNumberText,
        onValueChange = { newPhoneNumber ->
            if (newPhoneNumber.length <= 11 && newPhoneNumber.all { it.isDigit() }) {
                onPhoneNumberChange(newPhoneNumber)
            }
        },
        modifier = modifier,
        shape = shape,
        singleLine = true,
        isError = isValidPhoneNumber(phoneNumberText) && phoneNumberText.length == 11,
        textStyle = LocalTextStyle.current.copy(fontSize = 21.sp, fontWeight = FontWeight.Bold),
        label = {
            Text(text = stringResource(R.string.phoneNumber_text))
        },
        placeholder = {
            Text(text = stringResource(R.string.enterPhoneNumber_text))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Phone,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(25.dp)
            )
        },
        trailingIcon = {
            if (phoneNumberText.isNotEmpty()) {
                IconButton(
                    onClick = { onCloseClick() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
            }
        },
        visualTransformation = phoneNumberVisualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        )
    )
}

@Composable
fun SmsCodeTextField(
    codeText: String,
    onValueChange: (String) -> Unit,
    isCountingDown: Boolean,
    remainingTime: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    imeAction: ImeAction = ImeAction.Next,
    onSendClick: () -> Unit
) {
    TextField(
        value = codeText,
        onValueChange = { newCodeText ->
            onValueChange(newCodeText)
        },
        modifier = modifier,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 21.sp, fontWeight = FontWeight.Bold
        ),
        placeholder = {
            Text(text = stringResource(R.string.enterSmsCode_text))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Textsms,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(25.dp)
            )
        },
        trailingIcon = {
            FilledTonalButton(
                onClick = {
                    if (!isCountingDown) {
                        onSendClick()
                    }
                }, enabled = enabled
            ) {
                val annotatedString = buildAnnotatedString {
                    if (!isCountingDown) {
                        withStyle(style = SpanStyle(fontSize = 16.sp)) {
                            append(stringResource(R.string.getSmsCode_text))
                        }
                    } else {
                        withStyle(style = SpanStyle(fontSize = 17.sp, color = MyLightBlueTextColor)) {
                            append("${remainingTime}s")
                        }
                        withStyle(style = SpanStyle(fontSize = 16.sp)) {
                            append(stringResource(R.string.afterResend_text))
                        }
                    }
                }
                Text(text = annotatedString)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, imeAction = imeAction
        ),
        singleLine = true,
        shape = shape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

/**
 * 自定义验证码输入框，此输入框样式为每个验证码数字单独一个验证码框显示
 * @param codeValue 验证码文本
 * @param onValueChange 当输入服务更新文本时触发的回调函数更新后的文本作为回调函数的一个参数
 * @param codeTextLength 验证码的长度
 */
@ExperimentalLayoutApi
@Composable
fun SeparateVerificationCodeTextField(
    codeValue: String,
    onValueChange: (String) -> Unit,
    codeTextLength: Int = 4
) {
    val blinkInterval = 500L
    var isVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            isVisible = !isVisible
            delay(blinkInterval)
        }
    }

    BasicVerificationCodeTextField(
        codeText = codeValue,
        onValueChange = onValueChange,
        codeTextLength = codeTextLength
    ) { codeLength, codeIndex, code ->
        val cardSize = remember { (300.dp / codeLength) }
        val textSize = remember { (cardSize.value / 2f).sp }

        val isCode = codeIndex < code.length
        val verificationCodeState = when {
            isCode -> VerifyCodeState.ENTERED
            codeIndex == code.length -> VerifyCodeState.INPUTTING
            else -> VerifyCodeState.PENDING
        }

        val textColor = when (verificationCodeState) {
            VerifyCodeState.ENTERED -> Color.White
            VerifyCodeState.INPUTTING -> VerifyCodeEntered
            VerifyCodeState.PENDING -> Color.LightGray
        }

        val cardColor = when (verificationCodeState) {
            VerifyCodeState.ENTERED -> VerifyCodeEntered
            VerifyCodeState.INPUTTING -> MaterialTheme.colorScheme.inverseOnSurface
            VerifyCodeState.PENDING -> VerifyCodePending
        }

        val cardElevation = when (verificationCodeState) {
            VerifyCodeState.ENTERED -> 3.dp
            VerifyCodeState.INPUTTING -> 6.dp
            VerifyCodeState.PENDING -> 0.dp
        }

        key(cardElevation) {
            Card(
                modifier = Modifier.size(cardSize),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when (verificationCodeState) {
                        VerifyCodeState.ENTERED -> {
                            Text(
                                text = codeValue[codeIndex].toString(),
                                color = textColor,
                                fontSize = textSize,
                                textAlign = TextAlign.Center
                            )
                        }

                        VerifyCodeState.INPUTTING -> {
                            this@Card.AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(tween(300)),
                                exit = fadeOut(tween(300))
                            ) {
                                Text(
                                    text = "|",
                                    color = textColor,
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

/**
 * 验证码输入框
 * @param codeText 验证码文本
 * @param onValueChange 当输入服务更新文本时触发的回调函数更新后的文本作为回调函数的一个参数
 * @param modifier Modifier
 * @param codeTextLength 验证码文本长度
 * @param verificationCodeBox @Composable 自定义验证码输入框样式
 */
@ExperimentalLayoutApi
@Composable
private fun BasicVerificationCodeTextField(
    codeText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    codeTextLength: Int,
    verificationCodeBox: @Composable RowScope.(codeLength: Int, codeIndex: Int, codeText: String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardActions = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = codeText,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(20.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    keyboardActions?.show()
                }
            },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done
        ),
        decorationBox = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (index in 0 until codeTextLength) {
                    verificationCodeBox(codeTextLength, index, codeText)
                }
            }
        }
    )
}

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"
)
@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"

)
@Composable
fun TextFieldPreview() {
    JetItineraryTheme {
        Column(
            modifier = Modifier.height(350.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Account(
                label = "账号",
                userState = UserState()
            )
            Password(
                label = "密码",
                passwordState = PasswordState()
            )
            PasswordTextField(
                label = "密码",
                passwordState = PasswordState()
            )
            PhoneNumberOutlinedTextField(
                phoneNumberText = "手机号",
                onPhoneNumberChange = {},
                phoneNumberVisualTransformation = PhoneNumberVisualTransformation("")
            )
            PhoneNumberTextField(
                phoneNumberText = "手机号",
                onPhoneNumberChange = {},
                phoneNumberVisualTransformation = PhoneNumberVisualTransformation("")
            )
        }
    }
}

@ExperimentalLayoutApi
@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"
)
@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "zh-rCN",
    showBackground = true,
    device = "spec:width=1440px,height=3200px,dpi=560"

)
@Composable
fun VerificationCodeTextFieldPreview() {
    JetItineraryTheme {
        SeparateVerificationCodeTextField(
            codeValue = "",
            onValueChange = {},
            codeTextLength = 4
        )
    }
}