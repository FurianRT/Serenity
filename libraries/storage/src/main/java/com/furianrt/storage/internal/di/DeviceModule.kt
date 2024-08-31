package com.furianrt.storage.internal.di

import com.furianrt.storage.internal.database.notes.repositories.AppMediaRepository
import com.furianrt.storage.api.repositories.DeviceMediaRepository
import com.furianrt.storage.internal.device.repositories.AppMediaRepositoryImp
import com.furianrt.storage.internal.device.repositories.DeviceMediaRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DeviceModule {

    @Binds
    @Singleton
    fun deviceMediaRepository(imp: DeviceMediaRepositoryImp): DeviceMediaRepository

    @Binds
    @Singleton
    fun appMediaRepository(imp: AppMediaRepositoryImp): AppMediaRepository
}