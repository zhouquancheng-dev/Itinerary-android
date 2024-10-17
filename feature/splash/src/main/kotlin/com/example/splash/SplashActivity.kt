package com.example.splash

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
import com.aleyn.router.util.navigator
import com.example.common.data.Router.ROUTER_LOGIN_ACTIVITY
import com.example.common.data.Router.ROUTER_MAIN_ACTIVITY
import com.example.common.util.ext.startAcWithIntent
import com.example.splash.vm.Event
import com.example.splash.vm.SplashViewModel
import com.example.ui.dialog.AcceptPrivacyDialog
import com.example.ui.theme.JetItineraryTheme
import dagger.hilt.android.AndroidEntryPoint

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
                    vm.eventFlow.collect { event ->
                        when (event) {
                            is Event.FinishAc -> finish()
                            is Event.StartWelcome -> {
                                startAcWithIntent<WelcomeActivity>()
                            }
                            is Event.StartMain -> {
                                LRouter.navigator(ROUTER_MAIN_ACTIVITY)
                            }
                            is Event.StartLogin -> {
                                LRouter.navigator(ROUTER_LOGIN_ACTIVITY)
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