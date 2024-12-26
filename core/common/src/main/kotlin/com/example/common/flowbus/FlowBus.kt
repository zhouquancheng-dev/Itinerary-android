package com.example.common.flowbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.common.config.AppConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object FlowBus {
    const val TAG: String = "FlowBus"

    val events = ConcurrentHashMap<Class<*>, MutableSharedFlow<Any>>()
    val stickyEvents = ConcurrentHashMap<Class<*>, MutableSharedFlow<Any>>()

    // 异步任务执行的线程池
    val executorService: ExecutorService = Executors.newCachedThreadPool()
    // 定时任务的线程池
    private val scheduledExecutorService = Executors.newScheduledThreadPool(1)

    // 全局错误处理器
    var errorHandler: ((Throwable) -> Unit)? = null

    init {
        // 定时清理未使用的事件，每半小时执行一次清理任务
        scheduledExecutorService.scheduleWithFixedDelay(
            { cleanUp() }, 1, 30, TimeUnit.MINUTES
        )
    }

    inline fun <reified T : Any> getOrCreateFlow(sticky: Boolean): MutableSharedFlow<Any> {
        val flowKey = if (sticky) stickyEvents else events
        return flowKey.computeIfAbsent(T::class.java) {
            MutableSharedFlow(if (sticky) 1 else 0, 1, BufferOverflow.DROP_OLDEST)
        }
    }

    /**
     * 清理未使用的事件流。
     * 定时清理已无订阅者的事件流。
     */
    private fun cleanUp() {
        cleanMap(events)
        cleanMap(stickyEvents)
    }

    /**
     * 清理映射中的未使用事件流。
     * @param eventMap 事件映射
     */
    private fun cleanMap(eventMap: ConcurrentHashMap<Class<*>, MutableSharedFlow<Any>>) {
        eventMap.entries.removeIf {
            it.value.subscriptionCount.value == 0
        }
    }

    /**
     * 发布单个事件。
     * @param event 要发布的事件
     * @param sticky 是否为粘性事件，默认为 false
     */
    inline fun <reified T : Any> post(event: T, sticky: Boolean = false) {
        val flow = getOrCreateFlow<T>(sticky)
        if (AppConfig.DEBUG) {
            Log.d(TAG, "Posting event: ${T::class.java.simpleName}, sticky: $sticky")
        }
        executorService.execute {
            try {
                flow.tryEmit(event as Any)
            } catch (e: Exception) {
                errorHandler?.invoke(e) ?: Log.e(TAG, "Error posting event: ${T::class.java.simpleName}", e)
            }
        }
    }

    /**
     * 批量发布多个事件。
     * @param events 要发布的事件列表
     * @param sticky 是否为粘性事件，默认为 false
     */
    inline fun <reified T : Any> post(events: List<T>, sticky: Boolean = false) {
        events.forEach { post(it, sticky) }
    }

    /**
     * 发布带有优先级的事件。
     * @param event 要发布的事件
     * @param sticky 是否为粘性事件，默认为 false
     * @param priority 事件的优先级，默认为 [Thread.NORM_PRIORITY]
     */
    inline fun <reified T : Any> postWithPriority(
        event: T,
        sticky: Boolean = false,
        priority: Int = Thread.NORM_PRIORITY
    ) {
        require(priority in Thread.MIN_PRIORITY..Thread.MAX_PRIORITY) {
            "Priority must be between ${Thread.MIN_PRIORITY} and ${Thread.MAX_PRIORITY}"
        }

        val flow = getOrCreateFlow<T>(sticky)
        if (AppConfig.DEBUG) {
            Log.d(TAG, "Posting event: ${T::class.java.simpleName}, sticky: $sticky")
        }
        executorService.execute {
            Thread.currentThread().priority = priority
            try {
                flow.tryEmit(event as Any)
            } catch (e: Exception) {
                errorHandler?.invoke(e) ?: Log.e(TAG, "Error posting event: ${T::class.java.simpleName}", e)
            }
        }
    }

    /**
     * 发布转换后的事件。
     * @param event 要发布的事件
     * @param sticky 是否为粘滞事件，默认为 false
     * @param converter 事件转换器，将事件 T 转换为 R
     */
    inline fun <reified T : Any, reified R : Any> convertAndPost(
        event: T,
        sticky: Boolean = false,
        noinline converter: (T) -> R
    ) {
        val convertedEvent = converter(event)
        post(convertedEvent, sticky)
    }

    /**
     * 订阅事件。
     * @param owner LifecycleOwner，事件订阅的生命周期所有者
     * @param sticky 是否订阅粘性事件，默认为 false
     * @param dispatcher 协程调度器，默认为 [Dispatchers.Default]
     * @param singleEvent 是否为一次性订阅，默认为 false
     * @param filter 事件过滤器，默认为 { true }
     * @param onEvent 事件处理函数
     * @return 返回一个 Job，用于管理该订阅的生命周期
     */
    inline fun <reified T : Any> subscribe(
        owner: LifecycleOwner,
        sticky: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        singleEvent: Boolean = false,
        noinline filter: (T) -> Boolean = { true },
        crossinline onEvent: (T) -> Unit
    ): Job {
        val flow = getOrCreateFlow<T>(sticky).asSharedFlow()
        val scope = owner.lifecycleScope

        val job = scope.launch(dispatcher) {
            flow.filterIsInstance<T>()
                .filter { filter(it) }
                .collect { event ->
                    if (AppConfig.DEBUG) {
                        Log.d(TAG, "Receiving event: ${T::class.java.simpleName}")
                    }
                    onEvent(event)
                    if (singleEvent) cancel()  // 如果是一次性事件，收集后取消订阅
                }
        }
        job.bindToLifecycle(owner.lifecycle)  // 绑定生命周期
        return job
    }

    /**
     * 获取事件历史记录。
     * @param sticky 是否获取粘性事件的历史记录，默认为 false
     * @return 返回该类型事件的历史记录列表
     */
    inline fun <reified T : Any> getEventHistory(sticky: Boolean = false): List<T> {
        val flow = getOrCreateFlow<T>(sticky)
        return flow.replayCache.filterIsInstance<T>()  // 获取已缓存的事件历史
    }

    /**
     * 设置全局错误处理器。
     * @param handler 错误处理函数
     */
    fun setGlobalErrorHandler(handler: (Throwable) -> Unit) {
        errorHandler = handler
    }

}