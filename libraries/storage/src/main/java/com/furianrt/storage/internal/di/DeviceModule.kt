package com.furianrt.storage.internal.di

import android.content.Context
import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.repositories.DeviceMediaRepository
import com.furianrt.storage.internal.device.repositories.DeviceMediaRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DeviceModule {

    @Provides
    @Singleton
    fun deviceMediaRepository(
        @ApplicationContext context: Context,
        dispatchersProvider: DispatchersProvider,
    ): DeviceMediaRepository = DeviceMediaRepositoryImp(
        context = context,
        dispatchers = dispatchersProvider,
    )
}