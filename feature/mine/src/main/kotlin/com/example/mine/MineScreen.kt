package com.example.mine

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.aleyn.router.LRouter
import com.aleyn.router.util.navArrival
import com.example.common.data.LoginState
import com.example.common.data.Router.ROUTER_LOGIN_ACTIVITY

@Composable
fun MineScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
