package com.furianrt.settings.internal.di

import com.furianrt.settings.internal.data.SettingsRepositoryImp
import com.furianrt.settings.internal.domain.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface SettingsModule {
    @Binds
    fun settingsRepository(imp: SettingsRepositoryImp): SettingsRepository
}