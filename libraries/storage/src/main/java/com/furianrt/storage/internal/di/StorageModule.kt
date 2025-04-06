package com.furianrt.storage.internal.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATA_STORE_NAME = "SerenityPrefs"
private val Context.contextDataStore by preferencesDataStore(DATA_STORE_NAME)

@Module
@InstallIn(SingletonComponent::class)
internal class StorageModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.contextDataStore
}