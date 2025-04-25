package com.furianrt.backup.internal.di

import com.furianrt.backup.internal.data.remote.google.DriveBackupRepository
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface BackupModule {
    @Binds
    @Singleton
    fun backupRepository(imp: DriveBackupRepository): BackupRepository

    companion object {
        @Provides
        @Singleton
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        }
    }
}
