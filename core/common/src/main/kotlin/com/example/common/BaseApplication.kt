package com.example.common

import android.app.Application
import android.content.Context

open class BaseApplication : Application() {

    companion object {
        private var instance: BaseApplication? = null

        fun getContext(): Context {
            // 如果 instance 为 null，则同步初始化 instance
            return instance ?: synchronized(this) {
                // 双重检查锁定，确保 instance 在多线程环境下被正确初始化
                instance ?: BaseApplication().also { instance = it }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        initQbSdk()
        initBugLy()
        initOtherComponent()
    }

    private fun initOtherComponent() {
        // 初始化其他的推送、图片或者三方的网络框架库
    }

    private fun initBugLy() {
        // bug 上报
    }

    private fun initQbSdk() {
        // x5 内核初始化接口
    }
}