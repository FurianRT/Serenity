package com.furianrt.settings.internal.di

import android.appwidget.AppWidgetManager
import android.content.Context
import com.furianrt.settings.internal.data.SettingsRepositoryImp
import com.furianrt.settings.internal.domain.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
internal interface SettingsModule {
    @Binds
    fun settingsRepository(imp: SettingsRepositoryImp): SettingsRepository

    companion object {

        @Provides
        fun provideAppWidgetManager(
            @ApplicationContext context: Context,
        ): AppWidgetManager = AppWidgetManager.getInstance(context)
    }
}