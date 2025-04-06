package com.furianrt.domain.repositories

interface DeviceInfoRepository {
    fun hasNetworkConnection(): Boolean
}