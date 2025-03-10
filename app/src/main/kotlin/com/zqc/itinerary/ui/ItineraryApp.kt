package com.zqc.itinerary.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
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
import com.example.common.data.Constants.LOGIN_DEEP_LINK
import com.example.common.util.ext.startDeepLink
import com.example.im.vm.TIMBaseViewModel
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

    val ivm = viewModel<TIMBaseViewModel>()
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
    LifecycleStartEffect(isOffline, lifecycleOwner) {
        ivm.observeIMLoginState(lifecycleOwner)
        ivm.multiTerminalLoginState(lifecycleOwner) {
            // 在线时 [票据过期] 或 [被踢下线]，跳转登录页重新登录
            context.startDeepLink(LOGIN_DEEP_LINK) {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            (context as Activity).finish()
        }
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
    val currentDestination = appState.currentDestinationAsState
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
//    val shouldShowBottomBar = currentDestination.shouldShowBottomBar(appState.bottomNavItems)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            navigationSuiteBar(
                appState = appState,
                currentDestination = currentDestination,
                onNavigateToDestination = appState::navigateToTopLevelDestination,
                totalUnreadCount = totalUnreadCount
            )
        },
        modifier = modifier,
        layoutType = layoutType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = NavigationBarDefaults.containerColor,
            navigationRailContainerColor = NavigationRailDefaults.ContainerColor
        ),
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