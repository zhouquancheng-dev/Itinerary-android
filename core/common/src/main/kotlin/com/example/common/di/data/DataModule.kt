package com.example.common.di.data

import com.example.common.di.network.ConnectivityManagerNetworkMonitor
import com.example.common.di.network.NetworkMonitor
import com.example.common.di.timezone.TimeZoneBroadcastMonitor
import com.example.common.di.timezone.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor

    @Binds
    fun binds(
        impl: TimeZoneBroadcastMonitor
    ): TimeZoneMonitor

}