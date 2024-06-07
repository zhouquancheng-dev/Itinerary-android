package com.zqc.itinerary

import com.example.common.BaseApplication
import com.example.common.util.DataStoreUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApplication : BaseApplication() {

    private val applicationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onCreate() {
        super.onCreate()
        initData()
        initAppConfig()
    }

    /**
     * 初始化各数据
     */
    private fun initData() {
        applicationScope.launch {
            DataStoreUtils.init(this@MyApplication)
        }
    }

    /**
     * 初始化App的配置信息
     */
    private fun initAppConfig() {

    }
}