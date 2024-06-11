package com.example.splash

import android.content.Intent
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.common.util.DataStoreUtils
import com.example.common.util.startAcWithIntent
import com.example.common.util.startDeepLink
import com.example.splash.ds.DsKey.IS_FIRST_TIME_LAUNCH
import com.example.splash.ds.DsKey.IS_PRIVACY_AGREE
import com.example.ui.dialog.AcceptPrivacyDialog
import com.example.ui.theme.JetItineraryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetItineraryTheme {
                var showDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val privacyAgreed = isPrivacyAgree()
                    if (privacyAgreed) {
                        delay(300)
                        navigateToActivity()
                    } else {
                        showDialog = true
                    }
                }

                SplashScreen(
                    showDialog = showDialog,
                    onAcceptRequest = {
                        updatePrivacyAgreeState()
                        showDialog = false
                        navigateToActivity()
                    },
                    onRejectRequest = {
                        showDialog = false
                        lifecycleScope.launch {
                            delay(100)
                            finish()
                        }
                    }
                )
            }
        }
    }

    private suspend fun isPrivacyAgree(): Boolean {
        return DataStoreUtils.getBooleanFlow(IS_PRIVACY_AGREE).first()
    }

    private fun updatePrivacyAgreeState() {
        lifecycleScope.launch {
            DataStoreUtils.putBoolean(IS_PRIVACY_AGREE, true)
        }
    }

    private suspend fun isFirstTimeLaunch(): Boolean {
        return DataStoreUtils.getBooleanFlow(IS_FIRST_TIME_LAUNCH, true).first()
    }

    private suspend fun isLogin(): Boolean {
        return DataStoreUtils.getBooleanFlow("IS_LOGIN").first()
    }

    private fun navigateToActivity() {
        val flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        lifecycleScope.launch {
            if (isFirstTimeLaunch()) {
                startAcWithIntent<WelcomeActivity> {
                    it.flags = flags
                }
            } else {
                if (!isLogin()) {
                    startDeepLink("login://main")
                } else {
                    startDeepLink("app://main")
                }
            }
            finish()
        }
    }

}

@Composable
private fun SplashScreen(
    showDialog: Boolean,
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
    Surface(color = colorResource(R.color.splash_bg)) {
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
    if (showDialog) {
        AcceptPrivacyDialog(onAcceptRequest, onRejectRequest)
    }
}

@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(showDialog = false, onAcceptRequest = {}, onRejectRequest = {})
}

@Preview(device = "id:pixel_6_pro", showBackground = true)
@Composable
fun AcceptPrivacyDialogPreview() {
    AcceptPrivacyDialog(onAcceptRequest = {}, onRejectRequest = {})
}
