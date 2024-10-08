package com.zqc.itinerary

import androidx.appcompat.app.AppCompatDelegate
import com.example.common.BaseApplication
import com.example.common.config.AppConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : BaseApplication() {

    override fun isDebug(): Boolean {
        return BuildConfig.IS_DEBUG
    }

    @AppCompatDelegate.NightMode
    override fun getSystemNightMode(): Int {
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    override fun initData() {
        super.initData()
        initAppConfig()
    }

    /**
     * 初始化App的配置信息
     */
    private fun initAppConfig() {
        AppConfig.IS_DEBUG = BuildConfig.IS_DEBUG
        AppConfig.APP_NAME = BuildConfig.APP_NAME
        AppConfig.APPLICATION_ID = BuildConfig.APPLICATION_ID
        AppConfig.PRIVACY_URL = BuildConfig.PRIVACY_URL
        AppConfig.USER_PROTOCOL_URL = BuildConfig.USER_PROTOCOL_URL
        AppConfig.FILING_NO = BuildConfig.FILING_NO
        AppConfig.JIGUANG_APPKEY = BuildConfig.JIGUANG_APPKEY
        AppConfig.TENCENT_IM_APP_ID = BuildConfig.TENCENT_IM_APP_ID.toInt()
    }

}