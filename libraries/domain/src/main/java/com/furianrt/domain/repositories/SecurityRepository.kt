package com.furianrt.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
    fun getPin(): Flow<String?>
    suspend fun setPin(pin: String?)
    fun getPinRequestDelayMinutes(): Flow<Int>
    suspend fun setPinRequestDelayMinutes(minutes: Int)
    fun isFingerprintEnabled(): Flow<Boolean>
    suspend fun setFingerprintEnabled(enabled: Boolean)
    fun getPinRecoveryEmail(): Flow<String?>
    suspend fun setPinRecoveryEmail(email: String)
}