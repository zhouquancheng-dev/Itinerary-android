package com.example.profile.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.aleyn.annotation.Route
import com.example.common.data.Router.ROUTER_PROFILE_INFO_ACTIVITY
import com.example.profile.ui.ProfileInfo
import com.example.ui.theme.JetItineraryTheme

@Route(path = ROUTER_PROFILE_INFO_ACTIVITY)
class ProfileInfoActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetItineraryTheme {
                ProfileInfo { finish() }
            }
        }
    }

}