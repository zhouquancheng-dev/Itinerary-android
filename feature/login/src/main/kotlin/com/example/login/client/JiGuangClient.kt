package com.example.login.client

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.jiguang.verifysdk.api.AuthPageEventListener
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jiguang.verifysdk.api.JVerifyUIConfig
import cn.jiguang.verifysdk.api.LoginSettings
import com.blankj.utilcode.util.LogUtils
import com.example.login.R
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JiGuangClient @Inject constructor() {

    interface AuthListener {
        fun onAuthEvent(event: Int, msg: String?)
        fun onLoginAuthResult(code: Int, content: String?, operator: String?, operatorReturn: Any?)
    }

    /**
     * 预取号
     */
    fun preLogin(context: Context) {
        // 判断网络环境是否支持一键登录
        if (!JVerificationInterface.checkVerifyEnable(context)) {
            LogUtils.i("当前网络环境不支持认证，请开启数据网络")
            return
        }

        if (JVerificationInterface.isInitSuccess()) {
            JVerificationInterface.preLogin(context, 5000) { code, content, _ ->
                LogUtils.d(if (code == 7000) "预取号成功" else "返回码: $code, message: $content")
            }
        }
    }

    /**
     * 拉起授权页
     */
    fun loginAuth(context: Context, authListener: AuthListener) {
        setDialogUIConfig(context)

        val settings = LoginSettings()
        settings.isAutoFinish = true // 设置登录完成后是否自动关闭授权页
        settings.timeout = 15 * 1000 // 设置超时时间，单位毫秒。合法范围（0，30000], 范围以外默认设置为 10000
        settings.authPageEventListener = object : AuthPageEventListener() {
            override fun onEvent(event: Int, msg: String?) {
                // 设置授权页事件监听
                LogUtils.d("onEvent: $event message: $msg")
                authListener.onAuthEvent(event, msg)
            }
        }

        // 拉起授权页面
        JVerificationInterface.loginAuth(context, settings) { code, content, operator, operatorReturn ->
            LogUtils.d(
                when (code) {
                    6000 -> "获取loginToken成功，token: $content"
                    6001 -> "获取loginToken失败"
                    else -> "返回码: $code, token: $content, 对应运营商: $operator, 运营商结果信息: $operatorReturn"
                }
            )
            authListener.onLoginAuthResult(code, content, operator, operatorReturn)
        }
    }

    /**
     * 自定义全屏授权页UI
     */
    private fun setFullUIConfig(context: Context) {
        val customToast = customToast(context as Activity)

        val uiConfig = JVerifyUIConfig
            .Builder()
            .setStatusBarColorWithNav(true)
            .setNavReturnImgPath("arrow_back_ios_24dp_white")
            .setNavReturnBtnOffsetX(20)
            .setNavReturnBtnWidth(24)
            .setNavReturnBtnHeight(24)
            .setNavText("登录")
            .setNavTextSize(21)
            .setNavColor(Color.parseColor("#6195F9"))
            .setLogoImgPath("ic_app_logo")
            .setLogoOffsetY(100)
            .setNumberSize(30)
            .setNumFieldOffsetY(200)
            .setNumberTextBold(true)
            .setSloganTextSize(15)
            .setSloganOffsetY(250)
            .setLogBtnText("一键登录")
            .setLogBtnImgPath("login_btn_img")
            .setLogBtnOffsetY(400)
            .setLogBtnHeight(50)
            .setLogBtnTextSize(20)
            .setAppPrivacyColor(Color.parseColor("#000000"), Color.parseColor("#6195F9"))
            .enableHintToast(true, customToast)
            .enablePrivacyCheckDialog(false)
            .setCheckedImgPath("check_box_24dp_selected")
            .setUncheckedImgPath("check_box_24dp_unselected")
            .setPrivacyCheckboxSize(24)
            .setPrivacyCheckboxInCenter(true)
            .setPrivacyText("登录即同意", "并使用本机号码登录")
            .setPrivacyTextSize(15)
            .setPrivacyWithBookTitleMark(true)
            .setPrivacyMarginL(30)
            .setPrivacyMarginR(30)
            .setIsPrivacyViewDarkMode(true)
            .setPrivacyStatusBarColorWithNav(true)
            .setPrivacyNavColor(Color.parseColor("#6195F9"))
            .setPrivacyNavTitleTextColor(Color.parseColor("#FFFFFF"))
            .build()

        JVerificationInterface.setCustomUIWithConfig(uiConfig)
    }

    /**
     * 自定义弹窗模式的授权页UI
     */
    private fun setDialogUIConfig(context: Context) {
        val customToast = customToast(context as Activity)

        val uiConfig = JVerifyUIConfig
            .Builder()
            .setAuthBGImgPath("bg")
            .setLogBtnWidth(200)
            .setLogBtnImgPath("login_btn_img")
            .enableHintToast(true, customToast)
            .enablePrivacyCheckDialog(false)
            .setNumberSize(24)
            .setSloganTextSize(12)
            .setCheckedImgPath("check_box_24dp_selected")
            .setUncheckedImgPath("check_box_24dp_unselected")
            .setPrivacyCheckboxSize(24)
            .setPrivacyCheckboxInCenter(true)
            .setPrivacyText("登录即同意", "并使用本机号码登录")
            .setPrivacyTextSize(12)
            .setPrivacyWithBookTitleMark(true)
            .setPrivacyMarginL(30)
            .setPrivacyMarginR(30)
            .setIsPrivacyViewDarkMode(true)
            .setPrivacyStatusBarColorWithNav(true)
            .setPrivacyNavColor(Color.parseColor("#6195F9"))
            .setPrivacyNavTitleTextColor(Color.parseColor("#FFFFFF"))
            .setDialogTheme(300, 380, 0, 0, false)
            .build()

        JVerificationInterface.setCustomUIWithConfig(uiConfig)
    }

    /**
     * 自定义 toast
     */
    private fun customToast(activity: Activity): Toast {
        val inflater = LayoutInflater.from(activity)
        val layout: View = inflater.inflate(
            com.example.ui.R.layout.custom_toast_layout,
            activity.findViewById(com.example.ui.R.id.custom_toast_container)
        )
        val text: TextView = layout.findViewById(com.example.ui.R.id.custom_toast_message)
        text.text = String.format(Locale.getDefault(), activity.getString(R.string.login_error2))

        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_SHORT
        @Suppress("DEPRECATION")
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)

        return toast
    }

}