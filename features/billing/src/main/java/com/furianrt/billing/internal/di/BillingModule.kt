package com.furianrt.billing.internal.di

import com.android.billingclient.api.PurchasesUpdatedListener
import com.furianrt.billing.internal.data.repository.BillingRepositoryImp
import com.furianrt.billing.internal.domain.repository.BillingRepository
import com.furianrt.domain.managers.SerenityPlusProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface BillingModule {

    @Binds
    @Singleton
    fun billingRepository(imp: BillingRepositoryImp): BillingRepository

    @Binds
    @Singleton
    fun serenityPlusProvider(imp: BillingRepositoryImp): SerenityPlusProvider

    @Binds
    @Singleton
    fun purchasesUpdatedListener(imp: BillingRepositoryImp): PurchasesUpdatedListener
}