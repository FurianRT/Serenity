package com.furianrt.domain.repositories

import kotlinx.coroutines.flow.Flow

interface AppInfoRepository {
    fun getAppLaunchCount(): Flow<Int>
    suspend fun updateAppLaunchCount(count: Int)

    fun isOnboardingShown(): Flow<Boolean>
    suspend fun setOnboardingShown()
}