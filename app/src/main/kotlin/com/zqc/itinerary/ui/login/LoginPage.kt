package com.zqc.itinerary.ui.login

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundColorsBrush
import com.zqc.itinerary.R
import com.zqc.itinerary.ui.login.component.Account
import com.zqc.itinerary.ui.login.component.Password
import com.zqc.itinerary.ui.login.obj.PasswordState
import com.zqc.itinerary.ui.login.obj.UserState
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.MyLightBlueTextColor
import com.example.ui.theme.PhoneBackgroundColor
import com.example.ui.theme.WeChatBackgroundColor

/**
 * 在浏览器中打开Url
 */
private fun openUrlInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

/**
 * 登录页
 * @param onLoginClick 登录请求事件
 * @param navigateEnterPhoneClick 导航至手机登录页事件
 */
@ExperimentalComposeUiApi
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    openAlertDialog: Boolean,
    onLoginClick: (username: String, password: String) -> Unit,
    navigateRegisterClick: () -> Unit,
    navigateForgetPwdClick: () -> Unit,
    navigateEnterPhoneClick: () -> Unit
) {
    val context = LocalContext.current

    val brush = remember { Brush.horizontalGradient(BackgroundColorsBrush) }
    val userState = remember { UserState() }
    val passwordState = remember { PasswordState() }

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(false) }

    val textFieldModifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .padding(horizontal = 30.dp)

//    ProgressIndicatorDialog(
//        openAlertDialog = openAlertDialog,
//        dialogText = "正在登录..."
//    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = brush)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            painter = painterResource(R.drawable.bg_trip),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

        Card(
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Box (modifier = Modifier.height(150.dp)) {
                    Account(
                        label = stringResource(R.string.account_text),
                        userState = userState,
                        modifier = textFieldModifier.align(Alignment.TopCenter),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Password(
                        label = stringResource(R.string.pwd_text),
                        passwordState = passwordState,
                        modifier = textFieldModifier.align(Alignment.BottomCenter),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                TextButton(
                    onClick = { navigateForgetPwdClick() },
                    modifier = Modifier
                        .padding(end = 30.dp)
                        .align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(R.string.forgetPassword_text),
                        color = MyLightBlueTextColor,
                        fontSize = 13.sp
                    )
                }

                FilledTonalButton(
                    onClick = {
                        if (selectedOption) {
                            onLoginClick(userState.text, passwordState.text)
                        } else {
                            Toast.makeText(context, "请阅读并勾选相关协议", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 30.dp),
                    enabled = userState.text.isNotBlank() && passwordState.text.isNotBlank()
                ) {
                    Text(text = stringResource(R.string.login_text), fontSize = 20.sp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption,
                        onClick = { onOptionSelected(!selectedOption) }
                    )
                    AnnotatedClickableText(context)
                }

                OtherLoginContent(
                    onPhoneClick = { navigateEnterPhoneClick() },
                    onWechatClick = {}
                )

                TextButton(
                    onClick = { navigateRegisterClick() }
                ) {
                    Text(text = stringResource(R.string.register_text))
                }
            }
        }
    }
}

@Composable
private fun AnnotatedClickableText(context: Context) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append("我已阅读并同意")
        }

        pushStringAnnotation(tag = "registration_agreement", annotation = "https://developer.android.com")
        withStyle(style = SpanStyle(color = MyLightBlueTextColor, fontWeight = FontWeight.Bold)) {
            append("《驴游用户注册协议》")
        }
        pop()

        pushStringAnnotation(tag = "privacy_policy", annotation = "https://zyuxr.top")
        withStyle(style = SpanStyle(color = MyLightBlueTextColor, fontWeight = FontWeight.Bold)) {
            append("《法律声明及隐私权政策》")
        }
        pop()

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append("，我承诺接受并同意协议中的条款。")
        }
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "registration_agreement", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    Log.d("Clicked Registration_agreement URL", annotation.item)
                    openUrlInBrowser(context, annotation.item)
                }
            annotatedText.getStringAnnotations(tag = "privacy_policy", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    Log.d("Clicked Privacy_policy URL", annotation.item)
                    openUrlInBrowser(context, annotation.item)
                }
        }
    )
}

@Composable
private fun OtherLoginContent(
    modifier: Modifier = Modifier,
    onPhoneClick: () -> Unit,
    onWechatClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.otherLogin_text), color = Color.Gray)
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(R.drawable.ic_phone),
                contentDescription = stringResource(R.string.phoneLogin_text),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(color = PhoneBackgroundColor)
                    .clickable { onPhoneClick() }
                    .padding(10.dp)
            )
            Image(
                painter = painterResource(R.drawable.ic_wechat),
                contentDescription = stringResource(R.string.wechat_text),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(color = WeChatBackgroundColor)
                    .clickable { onWechatClick() }
                    .padding(10.dp)
            )
        }
    }
}

@ExperimentalComposeUiApi
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
fun LoginPagePreview() {
    JetItineraryTheme {
        LoginPage(
            openAlertDialog = true,
            onLoginClick = { _, _ -> },
            navigateEnterPhoneClick = {},
            navigateRegisterClick = {},
            navigateForgetPwdClick = {}
        )
    }
}