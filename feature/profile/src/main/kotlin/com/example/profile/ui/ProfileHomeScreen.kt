package com.example.profile.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.data.Constants.LOGIN_DEEP_LINK
import com.example.common.util.ext.startDeepLink
import com.example.profile.R
import com.example.profile.vm.ProfileViewModel
import com.example.ui.coil.LoadAsyncImage
import com.example.ui.components.VerticalSpacer
import com.example.ui.components.noRippleClickable
import com.example.ui.components.symbols.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHomeScreen(
    profileVm: ProfileViewModel,
    onProfileInfo: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val profileInfo by profileVm.profile.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        profileVm.getUserInfo(lifecycleOwner)
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
                            contentDescription = "setting"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
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
            Column(
                modifier = Modifier.noRippleClickable { onProfileInfo() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadAsyncImage(
                    model = profileInfo.firstOrNull()?.faceUrl ?: R.drawable.ic_default_face,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                VerticalSpacer(8.dp)
                Text(
                    text = profileInfo.firstOrNull()?.nickName ?: "",
                    fontSize = 21.sp
                )
            }

            Button(
                onClick = {
                    profileVm.logout {
                        context.startDeepLink(LOGIN_DEEP_LINK)
                        (context as Activity).finish()
                    }
                }
            ) {
                Text("退出登录")
            }
        }
    }
}
