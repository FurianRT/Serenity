package com.furianrt.backup.internal.di

import android.content.Context
import android.content.Intent
import com.furianrt.backup.internal.data.remote.google.DriveBackupRepository
import com.furianrt.backup.internal.domain.ServiceLauncher
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.backup.internal.services.NotesBackupService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

        @Provides
        @Singleton
        fun provideServiceLauncher(
            @ApplicationContext context: Context,
        ): ServiceLauncher = object : ServiceLauncher {
            override fun launchBackupService() {
                val intent = Intent(context, NotesBackupService::class.java)
                intent.putExtra(NotesBackupService.EXTRA_IS_BACKUP, true)
                context.startService(intent)
            }

            override fun launchRestoreService() {
                val intent = Intent(context, NotesBackupService::class.java)
                intent.putExtra(NotesBackupService.EXTRA_IS_BACKUP, false)
                context.startService(intent)
            }
        }
    }
}
