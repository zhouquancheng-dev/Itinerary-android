package com.example.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.login.component.UserTopAppBar
import com.example.login.obj.ConfirmPasswordState
import com.example.login.obj.PasswordState
import com.example.ui.theme.JetItineraryTheme
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun ResetPasswordPage(
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()

    val passwordState = remember { PasswordState() }
    val confirmPasswordState = remember { ConfirmPasswordState(passwordState) }

    val keyboardActions = LocalSoftwareKeyboardController.current
    val focusStateManger = LocalFocusManager.current

    val textFieldModifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .padding(horizontal = 30.dp)

    Scaffold(
        topBar = {
            UserTopAppBar(
                title = stringResource(R.string.forgetPassword_text1)
            ) {
                onBackClick()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = stringResource(R.string.resetPwd_text),
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp, start = 30.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(35.dp))

            FilledTonalButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 30.dp),
                enabled = passwordState.isValid && confirmPasswordState.isValid
            ) {
                Text(
                    text = stringResource(R.string.finish_text),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
fun ResetPasswordPagePreview() {
    JetItineraryTheme {
        ResetPasswordPage {}
    }
}