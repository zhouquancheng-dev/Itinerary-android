package com.example.network.captcha

import android.content.Context
import android.util.Log
import com.example.model.CaptchaResponse
import com.geetest.captcha.GTCaptcha4Client
import com.geetest.captcha.GTCaptcha4Config
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MyGTCaptcha4Client {

    private var gtCaptcha4Client: GTCaptcha4Client? = null

    private lateinit var captchaListener: CaptchaListener

    /**
     * 给外部提供的验证结果回调接口
     * @property onSuccess 验证成功
     * @property onFailure 验证失败
     * @property onError 验证错误
     */
    interface CaptchaListener {
        fun onSuccess(captchaResponse: CaptchaResponse)
        fun onFailure(message: String) {}
        fun onError(error: String) {}
    }

    // 设置监听器
    fun setCaptchaListener(listener: CaptchaListener) {
        captchaListener = listener
    }

    // 初始化验证码
    fun initCaptcha(context: Context, captchaId: String) {
        val gtCaptcha4Config = GTCaptcha4Config.Builder()
            .setDebug(false) // 线上需置为 false
            .setLanguage("zh")  // 指定语言
            .setTimeOut(10000)  // 设置超时，单位 ms
            .setCanceledOnTouchOutside(true)    // 点击区域外是否消失
            .build()
        gtCaptcha4Client = GTCaptcha4Client.getClient(context).init(captchaId, gtCaptcha4Config)
    }

    // 开始验证流程，启动并显示
    fun launchWithCaptcha():MyGTCaptcha4Client {
        gtCaptcha4Client
            ?.addOnSuccessListener { status, response ->
                if (status) {
                    val moshi = Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                    val adapter = moshi.adapter(CaptchaResponse::class.java)
                    val captchaResponse = adapter.fromJson(response)

                    Log.d("GTCaptcha4Client", "launchWithCaptcha response: $captchaResponse")

                    if (this::captchaListener.isInitialized) {
                        if (captchaResponse != null) {
                            captchaListener.onSuccess(captchaResponse)
                        }
                    }
                } else {
                    Log.d("GTCaptcha4Client", "launchWithCaptcha: 用户前端验证错误")
                    if (this::captchaListener.isInitialized) {
                        captchaListener.onFailure("前端验证错误")
                    }
                }
            }
            ?.addOnFailureListener { error ->
                Log.e("GTCaptcha4Client", "验证错误: $error")
                if (this::captchaListener.isInitialized) {
                    captchaListener.onError(error)
                }
            }
            ?.verifyWithCaptcha()
            ?.setLogEnable(false)
        return this
    }

    // 销毁验证资源，必须调用
    fun destroyCaptcha() {
        gtCaptcha4Client?.destroy()
    }

}