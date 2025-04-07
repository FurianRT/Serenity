package com.furianrt.security.internal.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
    fun getPin(): Flow<String?>
    suspend fun setPin(pin: String?)

    fun getPinRequestDelay(): Flow<Int>
    suspend fun setPinRequestDelay(delay: Int)

    fun isFingerprintEnabled(): Flow<Boolean>
    suspend fun setFingerprintEnabled(enabled: Boolean)

    fun getPinRecoveryEmail(): Flow<String?>
    suspend fun setPinRecoveryEmail(email: String)

    fun isBiometricAvailable(): Boolean

    fun getLastEmailSendTime(): Flow<Long>
    suspend fun setLastEmailSendTime(time: Long)
}