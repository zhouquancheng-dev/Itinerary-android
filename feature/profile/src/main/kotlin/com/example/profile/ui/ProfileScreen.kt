package com.example.profile.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aleyn.router.LRouter
import com.aleyn.router.util.navArrival
import com.example.common.data.LoginState
import com.example.common.data.Router.ROUTER_LOGIN_ACTIVITY
import com.example.profile.vm.ProfileViewModel
import com.example.ui.coil.LoadAsyncImage
import com.example.ui.components.VerticalSpacer
import com.example.ui.components.placeholder
import com.example.ui.components.symbols.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(profileVm: ProfileViewModel) {
    val context = LocalContext.current
    val profileInfo by profileVm.profile.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileVm.getUserInfo()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {},
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = AppIcons.Settings,
                            contentDescription = "setting",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (profileInfo.isNotEmpty()) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadAsyncImage(
                        model = profileInfo.first().faceUrl,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    VerticalSpacer(8.dp)
                    Text(
                        text = profileInfo.first().nickName,
                        fontSize = 21.sp
                    )
                }
            } else {
                ProfilePhotoPlaceholder()
            }

            Button(
                onClick = {
                    LoginState.isLoggedIn = false
                    LRouter.build(ROUTER_LOGIN_ACTIVITY).navArrival {
                        (context as? Activity)?.finish()
                    }
                }
            ) {
                Text("退出登录")
            }
        }
    }
}

@Composable
private fun ProfilePhotoPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            Modifier
                .size(75.dp)
                .clip(CircleShape)
                .placeholder(true))
        VerticalSpacer(8.dp)
        Spacer(
            Modifier
                .size(width = 90.dp, height = 24.dp)
                .clip(CircleShape)
                .placeholder(true))
    }
}