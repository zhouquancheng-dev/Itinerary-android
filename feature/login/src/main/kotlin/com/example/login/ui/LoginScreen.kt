package com.example.login.ui

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aleyn.router.LRouter
import com.aleyn.router.util.navArrival
import com.example.common.data.Router.ROUTER_MAIN_ACTIVITY
import com.example.common.util.ClickUtils.isFastClick
import com.example.ui.view.ToasterUtil.ToastStatus.WARN
import com.example.ui.view.ToasterUtil.showCustomToaster
import com.example.login.R
import com.example.login.components.LoginButton
import com.example.login.components.PhoneNumberTextField
import com.example.login.components.PrivacyContent
import com.example.login.state.DialogType
import com.example.login.state.PhoneNumberVisualTransformation
import com.example.login.theme.dividerBrush
import com.example.login.vm.LoginViewModel
import com.example.network.captcha.AliYunCaptchaClient
import com.example.ui.components.VerticalSpacer
import com.example.ui.components.click
import com.example.ui.dialog.ProgressIndicatorDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val imeVisible = WindowInsets.isImeVisible
    val dividerBrush = remember { Brush.horizontalGradient(dividerBrush) }
    var phoneNumberValue by remember { mutableStateOf("") }
    val phoneNumberVisualTransformation = remember(phoneNumberValue) {
        PhoneNumberVisualTransformation(phoneNumberValue)
    }
    var privacyChecked by remember { mutableStateOf(false) }
    val loginAuthState by loginViewModel.loginAuthState.collectAsStateWithLifecycle()
    val sendingVerifyCode by loginViewModel.sendingVerifyCode.collectAsStateWithLifecycle()

    val isOffline by loginViewModel.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.not_connected),
                duration = Indefinite,
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    loginViewModel.preLogin(context)
                    AliYunCaptchaClient.initCaptcha(context)
                }
                Lifecycle.Event.ON_DESTROY -> {
                    AliYunCaptchaClient.destroyCaptcha()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val login: () -> Unit = {
        if (!isFastClick()) {
            if (imeVisible) keyboardController?.hide()
            if (!privacyChecked) {
                showCustomToaster(context.getString(R.string.tick_protocol), WARN)
            } else {
                // 发送验证码
                loginViewModel.launchWithCaptcha(context) {
                    loginViewModel.sendSmsCode(phoneNumberValue) {
                        onNavigate(phoneNumberValue)
                    }
                }
            }
        }
    }
    val loginClick by rememberUpdatedState(login)

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 50.dp, start = 40.dp, end = 40.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(com.example.common.R.mipmap.ic_app_logo),
                contentDescription = null,
                modifier = Modifier.size(75.dp)
            )

            VerticalSpacer(30.dp)
            Text(
                text = stringResource(R.string.login_title),
                fontFamily = FontFamily(Font(com.example.ui.R.font.mashanzheng_regular)),
                fontSize = 28.sp
            )

            VerticalSpacer(50.dp)
            PhoneNumberTextField(
                value = phoneNumberValue,
                onValueChange = { newValue ->
                    if (newValue.isDigitsOnly() && newValue.length <= 11) {
                        phoneNumberValue = newValue
                    }
                },
                visualTransformation = phoneNumberVisualTransformation,
                onClearValue = { phoneNumberValue = "" }
            )

            VerticalSpacer(35.dp)
            PrivacyContent(
                checked = privacyChecked,
                onCheckedChange = { privacyChecked = it }
            )

            VerticalSpacer(70.dp)
            LoginButton(phoneNumberValue, sendingVerifyCode) {
                loginClick()
            }

            VerticalSpacer(65.dp)
            Spacer(
                modifier = Modifier
                    .width(75.dp)
                    .height(1.dp)
                    .background(brush = dividerBrush)
            )

            VerticalSpacer(45.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = painterResource(R.drawable.one_click_login),
                    contentDescription = null,
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .click(debounce = true) {
                            loginViewModel.loginAuth(context) {
                                LRouter.build(ROUTER_MAIN_ACTIVITY).navArrival {
                                    (context as? Activity)?.finish()
                                }
                            }
                        }
                )

                Image(
                    painter = painterResource(R.drawable.wechat_login),
                    contentDescription = null,
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .click(debounce = true) {
                            showCustomToaster("暂未接入", WARN)
                        }
                )
            }
        }
    }

    ProgressIndicatorDialog(
        showDialog = loginAuthState != DialogType.NONE,
        dialogText = loginAuthState.dialogText
    )
}