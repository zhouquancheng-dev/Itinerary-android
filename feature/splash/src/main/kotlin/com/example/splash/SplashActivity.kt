package com.example.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.common.BaseApplication
import com.example.common.util.startAcWithIntent
import com.example.common.util.startDeepLink
import com.example.common.data.DsKey.IS_FIRST_TIME_LAUNCH
import com.example.common.data.DsKey.IS_LOGIN_STATUS
import com.example.common.data.DsKey.IS_PRIVACY_AGREE
import com.example.common.util.DataStoreUtils.getBooleanFlow
import com.example.common.util.DataStoreUtils.putBoolean
import com.example.ui.dialog.AcceptPrivacyDialog
import com.example.ui.theme.JetItineraryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {

    private val bApplication by lazy { BaseApplication.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetItineraryTheme {
                var showDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (!isPrivacyAgree()) {
                        showDialog = true
                    } else {
                        navigateToActivity()
                    }
                }

                SplashScreen(
                    showDialog = showDialog,
                    onAcceptRequest = {
                        showDialog = false
                        navigateToActivity()
                    },
                    onRejectRequest = {
                        showDialog = false
                        finish()
                    }
                )
            }
        }
    }

    private suspend fun isFirstTimeLaunch() =
        getBooleanFlow(IS_FIRST_TIME_LAUNCH, true).first()

    private suspend fun isPrivacyAgree() = getBooleanFlow(IS_PRIVACY_AGREE).first()

    private suspend fun isLoginStatus() = getBooleanFlow(IS_LOGIN_STATUS).first()

    private fun navigateToActivity() {
        lifecycleScope.launch {
            putBoolean(IS_PRIVACY_AGREE, true)
            if (isFirstTimeLaunch()) {
                bApplication.initPrivacyRequiredSDKs()
                startAcWithIntent<WelcomeActivity>()
                finish()
            } else {
                delay(500)
                if (isLoginStatus()) {
                    startDeepLink("app://main")
                    finish()
                } else {
                    startDeepLink("login://main")
                    finish()
                }
            }
        }
    }
}

@Composable
private fun SplashScreen(
    showDialog: Boolean,
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
    if (showDialog) {
        AcceptPrivacyDialog(onAcceptRequest, onRejectRequest)
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(com.example.common.R.mipmap.ic_app_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = stringResource(R.string.splash_desc),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SplashScreenPreview() {
    JetItineraryTheme {
        SplashScreen(showDialog = false, onAcceptRequest = {}, onRejectRequest = {})
    }
}