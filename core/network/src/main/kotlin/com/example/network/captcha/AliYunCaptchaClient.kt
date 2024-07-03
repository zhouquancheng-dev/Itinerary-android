package com.example.network.captcha

import android.content.Context
import com.alicom.gtcaptcha4.AlicomCaptcha4Client
import com.alicom.gtcaptcha4.AlicomCaptcha4Config
import com.blankj.utilcode.util.LogUtils
import com.example.model.captcha.ErrorResponse
import com.example.model.captcha.FailureResponse
import com.example.model.captcha.SuccessResponse
import com.example.network.listener.CaptchaListener
import kotlinx.serialization.json.Json

object AliYunCaptchaClient {
    private var aliYunCaptchaClient: AlicomCaptcha4Client? = null

    private lateinit var captchaListener: CaptchaListener

    // 设置监听器
    fun setCaptchaListener(listener: CaptchaListener) {
        this.captchaListener = listener
    }

    // 预加载
    fun initCaptcha(context: Context) {
        val config = AlicomCaptcha4Config.Builder()
            .setDebug(false)
            .setLanguage("zh")
            .setTimeOut(10000)
            .setCanceledOnTouchOutside(true)
            .build()
        aliYunCaptchaClient = AlicomCaptcha4Client.getClient(context)
            .init("e1f7b5b035c1e1ff5d41f7322fe0ec96", config)
    }

    // 启动验证窗口
    fun launchWithCaptcha(): AliYunCaptchaClient {
        aliYunCaptchaClient?.addOnSuccessListener { status: Boolean, response: String ->
            val json = Json { ignoreUnknownKeys = true }
            if (status) {
                val successResponse = json.decodeFromString<SuccessResponse>(response)
                LogUtils.json("客户端验证成功", successResponse)

                captchaListener.onSuccess(successResponse)
            } else {
                val failureResponse = json.decodeFromString<FailureResponse>(response)
                LogUtils.json("客户端验证失败", failureResponse)

                captchaListener.onFailure("客户端验证错误")
            }
        }?.addOnFailureListener { error: String ->
            val errorResponse = Json.decodeFromString<ErrorResponse>(error)
            LogUtils.json(errorResponse)

            captchaListener.onError(error)
        }
        ?.verifyWithCaptcha()
        ?.setLogEnable(false)
        return this
    }

    // 销毁资源
    fun destroyCaptcha() {
        aliYunCaptchaClient?.destroy()
    }
}