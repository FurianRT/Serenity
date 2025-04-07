package com.furianrt.security.internal.di

import com.furianrt.security.api.LockAuthorizer
import com.furianrt.security.internal.data.SecurityDataStore
import com.furianrt.security.internal.domain.LockManager
import com.furianrt.security.internal.domain.repositories.SecurityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SecurityModule {
    @Binds
    @Singleton
    fun securityRepository(imp: SecurityDataStore): SecurityRepository

    @Binds
    @Singleton
    fun lockAuthorizer(imp: LockManager): LockAuthorizer
}