package com.zqc.itinerary.ui.login

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.zqc.itinerary.R
import com.zqc.itinerary.nav.LoginPageDestinations
import com.zqc.itinerary.ui.login.component.UserTopAppBar
import com.zqc.itinerary.ui.login.component.PhoneNumberOutlinedTextField
import com.zqc.itinerary.ui.login.obj.PhoneNumberVisualTransformation
import com.zqc.itinerary.ui.login.obj.formatPhoneNumber
import com.zqc.itinerary.ui.login.obj.isValidPhoneNumber
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.MyLightBlueTextColor

sealed class Options(
    val countryCode: String,
    @StringRes val phoneNumberLocation: Int,
    @DrawableRes val flagIcon: Int
) {
    data object China : Options(
        "+86",
        R.string.phoneNumberLocation_ChinaText,
        R.drawable.ic_flag_china
    )

    data object HongKong : Options(
        "+852",
        R.string.phoneNumberLocation_HongKongText,
        R.drawable.ic_flag_hongkong
    )
}

private fun openUrlInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

private fun isRoute(route: String?): Boolean {
    return route == LoginPageDestinations.PhoneNumberLoginPage.route
}

/**
 * 手机输入页面
 * @param onBackClick 返回上一步
 * @param onNavigateClick 导航至校验页
 */
@ExperimentalMaterial3Api
@Composable
fun EnterPhoneNumberPage(
    title: String,
    contentText: String,
    route: String?,
    modifier: Modifier = Modifier,
    prefixIcon: ImageVector = Icons.Rounded.Phone,
    onBackClick: () -> Unit,
    onNavigateClick: (String, String) -> Unit
) {
    val context = LocalContext.current

    var phoneNumberText by remember { mutableStateOf("") }
    val phoneNumberVisualTransformation = remember(phoneNumberText) {
        PhoneNumberVisualTransformation(phoneNumberText)
    }

    val optionsList = listOf(Options.China, Options.HongKong)
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(optionsList[0]) }

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            UserTopAppBar(title = title) {
                onBackClick()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = contentText,
                fontSize = 18.sp
            )
            if (isRoute(route)) {
                Text(
                    text = stringResource(R.string.inputPhoneLocation_text),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 30.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .menuAnchor(),
                    readOnly = true,
                    value = stringResource(selectedOptionText.phoneNumberLocation),
                    onValueChange = {},
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    prefix = {
                        Image(
                            painter = painterResource(selectedOptionText.flagIcon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(20.dp)
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    optionsList.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = stringResource(selectionOption.phoneNumberLocation)) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(selectionOption.flagIcon),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            PhoneNumberOutlinedTextField(
                phoneNumberText = phoneNumberText,
                onPhoneNumberChange = { newPhoneNumber ->
                    phoneNumberText = formatPhoneNumber(newPhoneNumber)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(horizontal = 30.dp),
                prefixIcon = prefixIcon,
                shape = RoundedCornerShape(50.dp),
                phoneNumberVisualTransformation = phoneNumberVisualTransformation,
                onCloseClick = {
                    phoneNumberText = ""
                }
            )
            if (isValidPhoneNumber(phoneNumberText) && phoneNumberText.length == 11) {
                Text(
                    text = stringResource(R.string.phoneNumberError_text),
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(55.dp))

            FilledTonalButton(
                onClick = {
                    if (isRoute(route)) {
                        if (selectedOption) {
                            onNavigateClick(selectedOptionText.countryCode, phoneNumberText)
                        } else {
                            Toast.makeText(context, "请阅读并勾选相关协议", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        onNavigateClick(selectedOptionText.countryCode, phoneNumberText)
                    }
                },
                enabled = !isValidPhoneNumber(phoneNumberText) && phoneNumberText.length == 11,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(55.dp)
            ) {
                Text(
                    text = stringResource(R.string.sendSmsCode_text),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(35.dp))

            if (isRoute(route)) {
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

        pushStringAnnotation(
            tag = "registration_agreement",
            annotation = "https://developer.android.com"
        )
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
            append("，我承诺接受并同意协议中的条款，未注册的手机号验证成功后将自动注册驴游账号。")
        }
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = "registration_agreement",
                start = offset,
                end = offset
            )
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

@ExperimentalMaterial3Api
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
fun PhonePagePreview() {
    JetItineraryTheme {
        EnterPhoneNumberPage(
            title = stringResource(R.string.phoneLogin_text),
            contentText = stringResource(R.string.inputPhone_text),
            route = LoginPageDestinations.PhoneNumberLoginPage.route,
            onBackClick = {},
            onNavigateClick = { _, _ -> }
        )
    }
}