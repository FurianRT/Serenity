package com.furianrt.storage.internal.preferences

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
import androidx.datastore.preferences.preferencesDataStore
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.repositories.SecurityRepository
import com.furianrt.storage.internal.encryption.SerenityCipher
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
private val KEY_RECOVERY_EMAIL_SEND_TIME = longPreferencesKey("recovery_email_send_time")
private val KEY_THEME_COLOR = stringPreferencesKey("theme_color")
private val KEY_DEFAULT_NOTE_FONT= stringPreferencesKey("default_note_font")
private val KEY_DEFAULT_NOTE_FONT_COLOR = stringPreferencesKey("default_note_font_color")
private val KEY_DEFAULT_NOTE_FONT_SIZE = intPreferencesKey("default_note_font_size")

@Singleton
internal class SerenityDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cipher: SerenityCipher,
) : SecurityRepository {
    private val Context.contextDataStore by preferencesDataStore(DATA_STORE_NAME)
    private val dataStore: DataStore<Preferences> = context.contextDataStore
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
        .map { prefs -> prefs[KEY_PIN_REQUEST_DELAY] ?: 5000 }

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

    fun getAppThemeColorId(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_THEME_COLOR] }

    suspend fun updateAppThemeColor(colorId: String) {
        dataStore.edit { prefs -> prefs[KEY_THEME_COLOR] = colorId }
    }

    fun getDefaultNoteFont(): Flow<NoteFontFamily> = dataStore.data
        .map { prefs -> NoteFontFamily.fromString(prefs[KEY_DEFAULT_NOTE_FONT]) }

    suspend fun setDefaultNoteFont(font: NoteFontFamily) {
        dataStore.edit { prefs -> prefs[KEY_DEFAULT_NOTE_FONT] = font.name }
    }

    fun getDefaultNoteFontColor(): Flow<NoteFontColor> = dataStore.data
        .map { prefs -> NoteFontColor.fromString(prefs[KEY_DEFAULT_NOTE_FONT_COLOR]) }

    suspend fun setDefaultNoteFontColor(color: NoteFontColor) {
        dataStore.edit { prefs -> prefs[KEY_DEFAULT_NOTE_FONT_COLOR] = color.name }
    }

    fun getDefaultNoteFontSize(): Flow<Int> = dataStore.data
        .map { prefs -> prefs[KEY_DEFAULT_NOTE_FONT_SIZE] ?: 15 }

    suspend fun setDefaultNoteFontSize(size: Int) {
        dataStore.edit { prefs -> prefs[KEY_DEFAULT_NOTE_FONT_SIZE] = size }
    }
}