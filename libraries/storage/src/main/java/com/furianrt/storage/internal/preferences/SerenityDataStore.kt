package com.furianrt.storage.internal.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.furianrt.domain.repositories.SecurityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DATA_STORE_NAME = "SerenityPrefs"

private val KEY_PIN = stringPreferencesKey("pin")
private val KEY_PIN_REQUEST_DELAY = intPreferencesKey("pin_request_delay")
private val KEY_IS_FINGERPRINT_ENABLED = booleanPreferencesKey("is_fingerprint_enabled")
private val KEY_PIN_RECOVERY_EMAIL = stringPreferencesKey("pin_recovery_email")

@Singleton
internal class SerenityDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : SecurityRepository {
    private val Context.contextDataStore by preferencesDataStore(DATA_STORE_NAME)
    private val dataStore: DataStore<Preferences> = context.contextDataStore

    override fun getPin(): Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_PIN] }

    override suspend fun setPin(pin: String?) {
        dataStore.edit { prefs ->
            if (pin.isNullOrBlank()) {
                prefs.remove(KEY_PIN)
            } else {
                prefs[KEY_PIN] = pin
            }
        }
    }

    override fun getPinRequestDelayMinutes(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[KEY_PIN_REQUEST_DELAY] ?: 0 }

    override suspend fun setPinRequestDelayMinutes(minutes: Int) {
        dataStore.edit { prefs -> prefs[KEY_PIN_REQUEST_DELAY] = minutes }
    }

    override fun isFingerprintEnabled(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[KEY_IS_FINGERPRINT_ENABLED] ?: true }

    override suspend fun setFingerprintEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_IS_FINGERPRINT_ENABLED] = enabled }
    }

    override fun getPinRecoveryEmail(): Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_PIN_RECOVERY_EMAIL] }

    override suspend fun setPinRecoveryEmail(email: String) {
        dataStore.edit { prefs -> prefs[KEY_PIN_RECOVERY_EMAIL] = email }
    }
}