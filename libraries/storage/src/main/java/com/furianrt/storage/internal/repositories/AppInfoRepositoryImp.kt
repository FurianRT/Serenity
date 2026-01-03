package com.furianrt.storage.internal.repositories

import com.furianrt.domain.repositories.AppInfoRepository
import com.furianrt.storage.internal.preferences.AppInfoDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AppInfoRepositoryImp @Inject constructor(
    private val appInfoDataStore: AppInfoDataStore,
) : AppInfoRepository {

    override fun getAppLaunchCount(): Flow<Int> = appInfoDataStore.getAppLaunchCount()

    override suspend fun updateAppLaunchCount(count: Int) {
        appInfoDataStore.setAppLaunchCount(count)
    }

    override fun isOnboardingShown(): Flow<Boolean> = appInfoDataStore.isOnboardingShown()

    override suspend fun setOnboardingShown() {
        appInfoDataStore.setOnboardingShown()
    }
}