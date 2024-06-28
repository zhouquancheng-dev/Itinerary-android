package com.example.common

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.alibaba.sdk.android.httpdns.InitConfig
import com.alibaba.sdk.android.httpdns.ranking.IPRankingBean
import com.blankj.utilcode.util.LogUtils
import com.example.common.data.DsKey.IS_PRIVACY_AGREE
import com.example.common.util.DataStoreUtils
import com.example.common.util.DataStoreUtils.getBooleanFlow
import com.hjq.toast.Toaster
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
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

        // 监听整个应用程序过程的生命周期
        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver())

        applicationScope.launch {
            initData()
            initNormalSdks()
            initQbSdk()
            if (getBooleanFlow(IS_PRIVACY_AGREE).first()) {
                initPrivacyRequiredSDKs()
            }
        }
        initBugLy()
    }

    inner class ApplicationLifecycleObserver : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            applicationScope.cancel()
        }
    }

    /**
     * 初始化数据
     */
    open fun initData() {
        DataStoreUtils.init(getInstance())
        LogUtils.getConfig().setLogSwitch(isDebug()).setLog2FileSwitch(false)
        Toaster.init(getInstance())
    }

    /**
     * 初始化不会调用隐私相关的sdk
     */
    open fun initNormalSdks() {
        //初始化配置，调用即可，不必处理返回值。
        InitConfig.Builder()
            // 配置是否启用https，默认http
            .setEnableHttps(true)
            // 配置服务请求的超时时长，毫秒，默认2秒，最大5秒
            .setTimeoutMillis(2 * 1000)
            // 配置是否启用本地缓存，默认不启用
            .setEnableCacheIp(true)
            // 自定义解析结果TTL
            .configCacheTtlChanger { host, _, ttl ->
                if (TextUtils.equals(host, "api.zyuxr.top")) {
                    ttl * 10
                } else ttl
            }
            // 配置是否允许返回过期IP，默认允许
            .setEnableExpiredIp(true)
            // 启用IP优选
            .setIPRankingList(arrayListOf(IPRankingBean("api.zyuxr.top", 9090)))
            // 针对哪一个account配置
            .buildFor("113753")
    }

    /**
     * 初始化第三方sdk
     */
    open fun initPrivacyRequiredSDKs() {
        // 极光认证一键登录sdk
        JVerificationInterface.setDebugMode(false)
        JVerificationInterface.init(this) { code, result ->
            LogUtils.d(if (code == 8000) "极光SDK初始化成功" else "返回码: $code 信息: $result")
        }
    }

    /**
     * bug 上报
     */
    open fun initBugLy() {
        CrashReport.initCrashReport(getInstance(), "1e43689b76", false)
    }

    /**
     * x5 内核初始化接口
     */
    open fun initQbSdk() {

    }
}