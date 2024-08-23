package com.zqc.itinerary.di

import android.app.Activity
import android.util.Log
import android.view.Window
import androidx.metrics.performance.JankStats
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object JankStatsModule {

    /**
     * 提供一个OnFrameListener实例，用于监听和处理帧事件。
     *
     * @return JankStats.OnFrameListener 实例，用于在发生帧抖动时进行日志记录。
     */
    @Provides
    fun providesOnFrameListener(): JankStats.OnFrameListener {
        return JankStats.OnFrameListener { frameData ->
            // 仅在发生帧抖动时进行处理。
            if (frameData.isJank) {
                // 目前只是记录抖动帧，更好的做法是将其报告给后端。
                Log.v("Itinerary Jank", frameData.toString())
            }
        }
    }

    /**
     * 提供Window实例的函数
     *
     * 此函数通过注解@Provides标记，表明其是一个提供依赖实例的方法，主要用于Dagger等依赖注入框架中
     * 它通过传入的Activity对象，获取并返回对应的Window实例，用于后续可能的窗口操作或定制化需求
     *
     * @param activity Activity实例，用于获取对应的Window对象
     * @return 返回传入的Activity的Window实例
     */
    @Provides
    fun providesWindow(activity: Activity): Window {
        return activity.window
    }

    /**
     * 提供JankStats实例的函数
     *
     * 此函数通过注解@Provides标记，表明其是一个提供依赖实例的方法，主要用于Dagger等依赖注入框架中
     * 它通过传入的Window对象，获取并返回对应的JankStats实例，用于后续可能的窗口操作或定制化需求
     *
     * @param window Window实例，用于获取对应的JankStats对象
     * @param frameListener JankStats.OnFrameListener实例，用于监听和处理帧事件
     * @return 返回传入的Window的JankStats实例
     */
    @Provides
    fun providesJankStats(
        window: Window,
        frameListener: JankStats.OnFrameListener,
    ): JankStats {
        return JankStats.createAndTrack(window, frameListener)
    }
}