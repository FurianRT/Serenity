package com.furianrt.security.internal.data

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.furianrt.security.internal.domain.repositories.SecurityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val KEY_PIN = stringPreferencesKey("pin")
private val KEY_PIN_REQUEST_DELAY = intPreferencesKey("pin_request_delay")
private val KEY_IS_FINGERPRINT_ENABLED = booleanPreferencesKey("is_fingerprint_enabled")
private val KEY_PIN_RECOVERY_EMAIL = stringPreferencesKey("pin_recovery_email")
private val KEY_RECOVERY_EMAIL_SEND_TIME = longPreferencesKey("recovery_email_send_time")

@Singleton
internal class SecurityDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val cipher: SerenityCipher,
) : SecurityRepository {
    private val biometricManager = BiometricManager.from(context)

    override fun getPin(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_PIN]?.let { cipher.decryptString(it) } }

    override suspend fun setPin(pin: String?) {
        dataStore.edit { prefs ->
            if (pin.isNullOrBlank()) {
                prefs.remove(KEY_PIN)
            } else {
                prefs[KEY_PIN] = cipher.encryptString(pin)
            }
        }
    }

    override fun getPinRequestDelay(): Flow<Int> = dataStore.data
        .map { prefs -> prefs[KEY_PIN_REQUEST_DELAY] ?: 15000 }

    override suspend fun setPinRequestDelay(delay: Int) {
        dataStore.edit { prefs -> prefs[KEY_PIN_REQUEST_DELAY] = delay }
    }

    override fun isFingerprintEnabled(): Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_IS_FINGERPRINT_ENABLED] ?: true }

    override suspend fun setFingerprintEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_IS_FINGERPRINT_ENABLED] = enabled }
    }

    override fun getPinRecoveryEmail(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_PIN_RECOVERY_EMAIL]?.let { cipher.decryptString(it) } }

    override suspend fun setPinRecoveryEmail(email: String) {
        dataStore.edit { prefs -> prefs[KEY_PIN_RECOVERY_EMAIL] = cipher.encryptString(email) }
    }

    override fun isBiometricAvailable(): Boolean {
        val result = biometricManager.canAuthenticate(BIOMETRIC_WEAK or BIOMETRIC_STRONG)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    override fun getLastEmailSendTime(): Flow<Long> = dataStore.data
        .map { prefs -> prefs[KEY_RECOVERY_EMAIL_SEND_TIME] ?: 0L }

    override suspend fun setLastEmailSendTime(time: Long) {
        dataStore.edit { prefs -> prefs[KEY_RECOVERY_EMAIL_SEND_TIME] = time }
    }
}