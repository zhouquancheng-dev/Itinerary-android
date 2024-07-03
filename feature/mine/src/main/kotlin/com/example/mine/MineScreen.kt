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
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.common.data.DatastoreKey.IS_LOGIN_STATUS
import com.example.common.data.DatastoreKey.TIM_USER_SIG
import com.example.common.util.DataStoreUtils.putBooleanSync
import com.example.common.util.DataStoreUtils.removeSync
import com.example.common.util.startDeepLink

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
                putBooleanSync(IS_LOGIN_STATUS, false)
                removeSync(stringPreferencesKey(TIM_USER_SIG))

                startDeepLink(context, "login://main")
                (context as? Activity)?.finish()
            }
        ) {
            Text("退出登录")
        }
    }
}
