package com.example.common.di

import com.example.common.di.AppDispatchers.Default
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {

    /**
     * 提供一个带有默认调度器的CoroutineScope，供全局使用。
     *
     * 通过@Provides、@Singleton和@ApplicationScope注解，表明该函数是一个提供器函数，提供的CoroutineScope将在应用生命周期内 singleton 使用。
     * 使用SupervisorJob()来创建一个可以自动连接的Job，当一个协程子任务失败时，不会取消其他子任务。
     * 使用@Dispatcher(Default)来指定使用默认的调度器，通常是 Dispatchers.Default，用于执行计算密集型任务。
     *
     * @param dispatcher CoroutineDispatcher实例，用于配置CoroutineScope的并发行为。
     * @return 返回一个配置好的CoroutineScope实例，它使用SupervisorJob和指定的调度器。
     */
    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

}
