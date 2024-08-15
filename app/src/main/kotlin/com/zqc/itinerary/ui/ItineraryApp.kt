package com.zqc.itinerary.ui

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyn.router.LRouter
import com.aleyn.router.util.navArrival
import com.example.common.data.Router.ROUTER_LOGIN_ACTIVITY
import com.example.im.vm.IMViewModel
import com.example.ui.components.AppBackground
import com.zqc.itinerary.R
import com.zqc.itinerary.ui.navigation.navigationSuiteBar

@Composable
fun ItineraryApp(
    appState: ItineraryAppState,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val ivm = viewModel<IMViewModel>()
    val totalUnreadCount by ivm.totalUnreadCount.collectAsStateWithLifecycle()

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.not_connected),
                duration = Indefinite,
            )
        }
    }
    LifecycleStartEffect(isOffline) {
        ivm.observeIMLoginState(lifecycleOwner) {
            // 在线时票据过期 或 被踢下线
            LRouter.build(ROUTER_LOGIN_ACTIVITY).navArrival {
                (context as? Activity)?.finish()
            }
        }
        ivm.getTotalUnreadCount(lifecycleOwner)

        onStopOrDispose {
            ivm.unregisterListener()
        }
    }

    AppBackground {
        App(
            appState = appState,
            snackbarHostState = snackbarHostState,
            windowAdaptiveInfo = windowAdaptiveInfo,
            totalUnreadCount = totalUnreadCount
        )
    }
}

@Composable
internal fun App(
    modifier: Modifier = Modifier,
    appState: ItineraryAppState,
    snackbarHostState: SnackbarHostState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    totalUnreadCount: Long
) {
    val navController = appState.navController
    val currentDestination = appState.currentDestination
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            navigationSuiteBar(
                appState,
                currentDestination,
                appState::navigateToTopLevelDestination,
                totalUnreadCount
            )
        },
        modifier = modifier,
        layoutType = layoutType,
        containerColor = Color.Transparent
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            AppNavGraph(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                navController = navController
            )
        }
    }
}