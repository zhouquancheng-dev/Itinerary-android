package com.example.profile.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.profile.R
import com.example.profile.fragment.GenderFragment
import com.example.profile.fragment.NickNameFragment
import com.example.profile.fragment.SelfSignatureFragment
import com.example.profile.ui.ProfileInfoPage
import com.example.ui.theme.JetItineraryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileInfoActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetItineraryTheme {
                ProfileInfoPage(
                    onBack = { finish() },
                    onNickName = { nickName ->
                        val bundle = Bundle().apply {
                            putString("key_nickname", nickName)
                        }
                        switchFragment(NickNameFragment(), bundle)
                    },
                    onGender = { gender ->
                        val bundle = Bundle().apply {
                            putInt("key_gender", gender)
                        }
                        switchFragment(GenderFragment(), bundle)
                    },
                    onSelfSignature = { selfSignature ->
                        val bundle = Bundle().apply {
                            putString("key_self_signature", selfSignature)
                        }
                        switchFragment(SelfSignatureFragment(), bundle)
                    }
                )
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 如果返回栈中有Fragment，就弹出返回栈，否则结束Activity
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun switchFragment(fragment: Fragment, data: Bundle? = null) {
        fragment.arguments = data
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_right
            )
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

}