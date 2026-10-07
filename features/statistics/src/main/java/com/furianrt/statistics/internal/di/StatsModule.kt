package com.furianrt.statistics.internal.di

import com.furianrt.statistics.internal.data.repository.StatsRepositoryImp
import com.furianrt.statistics.internal.domain.repository.StatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface StatsModule {
    @Binds
    fun billingRepository(imp: StatsRepositoryImp): StatsRepository
}