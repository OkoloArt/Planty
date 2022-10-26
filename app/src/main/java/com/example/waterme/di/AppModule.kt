package com.example.waterme.di

import com.example.waterme.adapter.PlantViewPagerAdapter
import com.example.waterme.utils.ConnectivityObserver
import com.example.waterme.utils.NetworkConnectivityObserver
import com.example.waterme.worker.WaterReminderWorker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun provideConnectivityObserver(networkConnectivityObserver: NetworkConnectivityObserver): ConnectivityObserver
}