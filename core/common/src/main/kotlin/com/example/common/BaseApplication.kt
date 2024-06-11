package com.example.common

import android.app.Application
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.blankj.utilcode.util.LogUtils
import com.example.common.util.DataStoreUtils
import com.hjq.toast.Toaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class BaseApplication : Application() {

    private val applicationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    abstract fun isDebug(): Boolean

    companion object {
        private var instance: BaseApplication? = null

        /**
         * 全局context
         */
        fun getInstance(): BaseApplication {
            return instance ?: throw IllegalStateException("Application is not created yet!")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initData()
        initNormalSdk()
        initThirdSdk()
        initQbSdk()
        initBugLy()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        applicationScope.launch {
            DataStoreUtils.init(getInstance())
            LogUtils.getConfig().setLogSwitch(isDebug())
            Toaster.init(getInstance())
        }
    }

    /**
     * 初始化不会调用隐私相关的sdk
     */
    private fun initNormalSdk() {

    }

    /**
     * 初始化第三方sdk
     */
    private fun initThirdSdk() {
        // 极光认证、一键登录sdk
        JVerificationInterface.setDebugMode(isDebug())
        JVerificationInterface.init(this) { code, result ->
            // code 8000 代表初始化成功，其他为失败
            LogUtils.d("极光认证SDK初始化：code: $code, msg: $result")
        }
    }

    /**
     * bug 上报
     */
    private fun initBugLy() {

    }

    /**
     * x5 内核初始化接口
     */
    private fun initQbSdk() {

    }

}