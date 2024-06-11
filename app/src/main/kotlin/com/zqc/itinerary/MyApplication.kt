package com.zqc.itinerary

import com.example.common.BaseApplication
import com.example.common.config.AppConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        initAppConfig()
    }

    override fun isDebug(): Boolean {
        return true
    }

    /**
     * 初始化App的配置信息
     */
    private fun initAppConfig() {
        AppConfig.IS_DEBUG = BuildConfig.IS_DEBUG
        AppConfig.APPLICATION_ID = BuildConfig.APPLICATION_ID
        AppConfig.PRIVACY_URL = BuildConfig.PRIVACY_URL
        AppConfig.USER_PROTOCOL_URL = BuildConfig.USER_PROTOCOL_URL
        AppConfig.FILING_NO = BuildConfig.FILING_NO
        AppConfig.JIGUANG_APPKEY = BuildConfig.JIGUANG_APPKEY
    }

}