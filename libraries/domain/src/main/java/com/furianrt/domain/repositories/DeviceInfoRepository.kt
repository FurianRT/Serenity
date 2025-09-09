package com.furianrt.domain.repositories

interface DeviceInfoRepository {
    fun hasNetworkConnection(): Boolean
    fun getDeviceModel(): String
    fun getDeviceLanguage(): String
    fun getAndroidVersion(): String
    fun getMarketUrl(): String
    fun getAppVersionName(): String
    fun getDeviceInfoText(): String
}