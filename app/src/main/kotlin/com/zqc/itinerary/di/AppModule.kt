package com.zqc.itinerary.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * 提供应用程序上下文 Context。
     *
     * 通过此函数提供的应用程序上下文将被用于注入和其他依赖项的访问。
     * 使用 @Provides 注解表明此函数是一个提供器成员，用于 Dagger 2 的依赖注入。
     * 使用 @Singleton 注解确保此提供器在应用程序生命周期内只生成一个实例。
     *
     * @param context 应用程序上下文，作为参数传入。
     * @return 返回传入的应用程序上下文。
     */
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

}