package com.example.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cn.jiguang.verifysdk.api.AuthPageEventListener
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jiguang.verifysdk.api.JVerifyUIConfig
import cn.jiguang.verifysdk.api.LoginSettings
import cn.jiguang.verifysdk.api.PrivacyBean

class LoginActivity : ComponentActivity() {

    private var finishPrelogin: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preLogin()
        loginAuth()
    }

    private fun preLogin() {
        // 判断网络环境是否支持一键登录
        val verifyEnable = JVerificationInterface.checkVerifyEnable(this)
        if (!verifyEnable) {
            Toast.makeText(this, "当前网络环境不支持认证，请使用数据网络", Toast.LENGTH_SHORT).show()
            return
        }

        // 判断SDK初始化状态
        if (JVerificationInterface.isInitSuccess()) {
            finishPrelogin = false
            // 预取号
            JVerificationInterface.preLogin(this, 5000) { code, content, operatorReturn ->
                finishPrelogin = true
                // code 7000 代表获取成功，其他为失败
                Log.d("preLogin", "code: $code, message: $content")
            }
        }
    }

    private fun loginAuth() {
        if (!finishPrelogin) return

        setUIConfig()

        val settings = LoginSettings()
        settings.isAutoFinish = true // 设置登录完成后是否自动关闭授权页
        settings.timeout = 15 * 1000 // 设置超时时间，单位毫秒。合法范围（0，30000], 范围以外默认设置为 10000
        settings.authPageEventListener = object : AuthPageEventListener() {
            override fun onEvent(cmd: Int, msg: String?) {
                // 设置授权页事件监听
            }
        }

        // 拉起授权页面
        JVerificationInterface.loginAuth(this, settings) { code, content, operator, operatorReturn ->
            // code返回码，6000 代表 loginToken 获取成功，6001 代表 loginToken 获取失败
            Log.d("loginAuth", "code: $code, token: $content, operator: $operator, operatorReturn: $operatorReturn")
        }
    }

    // UIConfig
    private fun setUIConfig() {
        val uiConfig = JVerifyUIConfig
            .Builder()
            .setAuthBGImgPath("art_onboarding_1")
            .setNavText("登录")
            .setNavTextColor(0xffffff)
//            .setNavReturnImgPath("umcsdk_return_bg")
            .setLogoWidth(70)
            .setLogoHeight(70)
            .setLogoHidden(false)
            .setNumberColor(0xff3333)
            .setLogBtnText("本机号码一键登录")
            .setLogBtnTextColor(0xffffff)
//            .setLogBtnImgPath("umcsdk_login_btn_bg")
//            .setPrivacyNameAndUrlBeanList(List<PrivacyBean>)
//            .setPrivacyNameAndUrlBeanList(List<PrivacyBean>)
//            .setUncheckedImgPath("umcsdk_uncheck_image")
//            .setCheckedImgPath("umcsdk_check_image")
            .setSloganTextColor(0xff9999)
            .setLogoOffsetY(50)
            .setLogoImgPath("ic_app_logo")
            .setNumFieldOffsetY(170)
            .setSloganOffsetY(230)
            .setLogBtnOffsetY(254)
            .setNumberSize(18)
            .setPrivacyState(false)
            .setNavTransparent(false)
            .build()

        JVerificationInterface.setCustomUIWithConfig(uiConfig)
    }

}