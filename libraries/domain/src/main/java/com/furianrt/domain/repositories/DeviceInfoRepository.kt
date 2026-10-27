package com.furianrt.domain.repositories

import kotlinx.coroutines.flow.StateFlow

interface DeviceInfoRepository {
    fun hasNetworkConnection(): Boolean
    fun getDeviceModel(): String
    fun getDeviceLanguage(): String
    fun getAndroidVersion(): String
    fun getMarketUrl(): String
    suspend fun getAppVersionName(): String
    suspend fun getDeviceInfoText(): String
    fun isPowerSaveModeEnabled(): StateFlow<Boolean>
    fun isDndModeEnabled(): StateFlow<Boolean>
}