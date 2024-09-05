package com.example.common

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import cn.jiguang.verifysdk.api.JVerificationInterface
import com.aleyn.router.LRouter
import com.alibaba.sdk.android.httpdns.InitConfig
import com.alibaba.sdk.android.httpdns.ranking.IPRankingBean
import com.blankj.utilcode.util.LogUtils
import com.example.common.config.AppConfig
import com.example.common.data.Constants.JG_TAG
import com.example.common.data.DatastoreKey.IS_PRIVACY_AGREE
import com.example.common.util.DataStoreUtils
import com.example.common.util.DataStoreUtils.getBooleanSync
import com.hjq.toast.Toaster
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.imsdk.v2.V2TIMLogListener
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class BaseApplication : Application() {

    private val applicationScope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.IO) }

    abstract fun isDebug(): Boolean

    @AppCompatDelegate.NightMode
    abstract fun getSystemNightMode(): Int

    inner class ApplicationLifecycleObserver : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            applicationScope.cancel()
        }
    }

    companion object {
        private var instance: BaseApplication? = null

        /**
         * 全局context
         */
        fun getInstance(): BaseApplication {
            return instance ?: throw IllegalStateException("Application is not created yet!")
        }

        /**
         * 获取当前是否为夜间模式
         */
        fun isModeNightYes(): Boolean {
            val uiModeManager = getInstance().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            return uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setDefaultNightMode(getSystemNightMode())
        observeLifecycle()

        // 并行初始化各个方法
        applicationScope.launch {
            initData()

            val initJobs = mutableListOf(
                async { initNormalSdks() },
                async(Dispatchers.Main) { initBugLy() }
            )

            // 如果用户已经同意隐私政策，则初始化需要隐私协议的SDK
            if (getBooleanSync(IS_PRIVACY_AGREE)) {
                initJobs += async {
                    initPrivacyRequiredSDKs()
                }
            }

            // 等待所有初始化任务完成
            initJobs.awaitAll()
        }
    }

    private fun observeLifecycle() {
        // 监听整个应用程序过程的生命周期
        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver())
    }

    /**
     * 初始化数据
     */
    open fun initData() {
        DataStoreUtils.init(getInstance())
        LogUtils.getConfig().setLogSwitch(isDebug()).setLog2FileSwitch(false)
        Toaster.init(getInstance())
        MMKV.initialize(getInstance())
        LRouter.init(getInstance())
        LRouter.setLogSwitch(isDebug())
    }

    /**
     * 初始化不会调用隐私相关的SDK
     */
    private fun initNormalSdks() {
        initAliHttpDNS()
    }

    /**
     * 初始化需要用户同意隐私政策的第三方SDK
     */
    open fun initPrivacyRequiredSDKs() {
        initJG()
        initTIM()
    }

    /**
     * bug 上报
     */
    private fun initBugLy() {
        CrashReport.initCrashReport(getInstance(), "1e43689b76", false)
    }

    /**
     * 初始化极光SDK
     */
    private fun initJG() {
        JVerificationInterface.setDebugMode(isDebug())
        JVerificationInterface.init(getInstance()) { code, result ->
            Log.i(JG_TAG, if (code == 8000) "极光SDK初始化成功" else "返回码: $code 信息: $result")
        }
    }

    /**
     * 初始化阿里云 https dns 加速
     */
    private fun initAliHttpDNS() {
        //初始化配置，调用即可，不必处理返回值
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
     * Tencent IM SDK
     * 使用无UI集成时需先初始化SDK
     */
    private fun initTIM() {
        val config = V2TIMSDKConfig().apply {
            logLevel = V2TIMSDKConfig.V2TIM_LOG_DEBUG
            logListener = object : V2TIMLogListener() {
                override fun onLog(logLevel: Int, logContent: String?) {
                    super.onLog(logLevel, logContent)
                }
            }
        }
        // 初始化SDK
        V2TIMManager.getInstance().initSDK(getInstance(), AppConfig.TENCENT_IM_APP_ID, config)
    }
}