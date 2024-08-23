package com.example.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    /**
     * 提供IO操作的协程调度器
     *
     * 该函数通过@Provides注解标记，指示其是一个依赖注入提供方
     * 使用@Dispatcher(AppDispatchers.IO)注解，指定该函数返回的调度器用于IO操作
     * 主要用于异步IO操作，如磁盘读写和网络请求，以提高应用程序的响应能力和性能
     *
     * @return CoroutineDispatcher 返回一个协程调度器，用于执行IO密集型任务
     */
    @Provides
    @Dispatcher(AppDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * 提供默认的协程调度器
     *
     * 该函数通过 @Provides 注解表明其是一个提供依赖注入的函数，用于提供一个默认的协程调度器。
     * 它使用了 @Dispatcher(AppDispatchers.Default) 注解，将该调度器标记为应用的默认调度器。
     *
     * @return CoroutineDispatcher 返回一个协程调度器实例，具体为 Dispatchers.Default，表示使用默认的协程调度策略。
     */
    @Provides
    @Dispatcher(AppDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

}
