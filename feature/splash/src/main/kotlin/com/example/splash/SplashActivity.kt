package com.example.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyn.router.LRouter
import com.example.common.data.Router.ROUTER_LOGIN_ACTIVITY
import com.example.common.data.Router.ROUTER_MAIN_ACTIVITY
import com.example.common.util.ext.setExitOnBackPressedCallback
import com.example.common.util.ext.startActivity
import com.example.splash.vm.Event
import com.example.splash.vm.SplashViewModel
import com.example.ui.dialog.AcceptPrivacyDialog
import com.example.ui.theme.JetItineraryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetItineraryTheme {
                val vm = viewModel<SplashViewModel>()
                val showDialog by vm.showDialog.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    vm.initPrivacyState()
                    delay(500)
                    vm.eventFlow.collect { event ->
                        when (event) {
                            is Event.FinishAc -> finish()
                            is Event.StartWelcome -> {
                                startActivity<WelcomeActivity>()
                                finish()
                            }
                            is Event.StartMain -> {
                                LRouter.build(ROUTER_MAIN_ACTIVITY)
                                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .navigation()
                            }
                            is Event.StartLogin -> {
                                LRouter.build(ROUTER_LOGIN_ACTIVITY)
                                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .navigation()
                            }
                        }
                    }
                }

                SplashScreen(
                    showDialog = showDialog,
                    onAcceptRequest = vm::acceptPrivacy,
                    onRejectRequest = vm::rejectPrivacy
                )
            }
        }

        // 禁用Splash页返回键
        setExitOnBackPressedCallback()
    }
}

@Composable
private fun SplashScreen(
    showDialog: Boolean,
    modifier: Modifier = Modifier,
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
    if (showDialog) {
        AcceptPrivacyDialog(onAcceptRequest, onRejectRequest)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@PreviewLightDark
@Composable
private fun SplashScreenPreview() {
    JetItineraryTheme {
        SplashScreen(showDialog = false, onAcceptRequest = {}, onRejectRequest = {})
    }
}