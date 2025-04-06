package com.furianrt.backup.internal.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.furianrt.backup.internal.data.BackupRepositoryImp
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.google.android.gms.auth.api.identity.AuthorizationClient
import com.google.android.gms.auth.api.identity.Identity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(ViewModelComponent::class)
internal interface BackupModule {
    @Binds
    @ViewModelScoped
    fun backupRepository(imp: BackupRepositoryImp): BackupRepository

    companion object {
        @Provides
        @ViewModelScoped
        fun provideAuthorizationClient(
            @ApplicationContext context: Context,
        ): AuthorizationClient = Identity.getAuthorizationClient(context)

        @Provides
        @ViewModelScoped
        fun provideCredentialManager(
            @ApplicationContext context: Context,
        ): CredentialManager = CredentialManager.create(context)

        @Provides
        @ViewModelScoped
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
    }
}
