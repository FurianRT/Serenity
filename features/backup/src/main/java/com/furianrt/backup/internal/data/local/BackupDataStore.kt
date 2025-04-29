package com.furianrt.backup.internal.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private val KEY_GOOGLE_ACCESS_TOKEN = stringPreferencesKey("google_access_token")
private val KEY_AUTO_BACKUP = booleanPreferencesKey("auto_backup")
private val KEY_AUTO_BACKUP_PERIOD = longPreferencesKey("auto_backup_period")
private val KEY_LAST_SYNC_DATE = stringPreferencesKey("last_sync_date")
private val KEY_CONFIRM_BACKUP = booleanPreferencesKey("confirm_backup")

@Singleton
internal class BackupDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun getGoogleAccessToken(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_GOOGLE_ACCESS_TOKEN] }

    suspend fun updateGoogleAccessToken(token: String) {
        dataStore.edit { prefs -> prefs[KEY_GOOGLE_ACCESS_TOKEN] = token }
    }

    fun isAutoBackupEnabled(): Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_AUTO_BACKUP] ?: false }

    suspend fun setAutoBackupEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_AUTO_BACKUP] = enabled }
    }

    fun getAutoBackupPeriod(): Flow<BackupPeriod> = dataStore.data
        .map { prefs ->
            BackupPeriod.fromValue(prefs[KEY_AUTO_BACKUP_PERIOD]) ?: BackupPeriod.OneDay
        }

    suspend fun setAutoBackupPeriod(period: BackupPeriod) {
        dataStore.edit { prefs -> prefs[KEY_AUTO_BACKUP_PERIOD] = period.value }
    }

    fun getLastSyncDate(): Flow<ZonedDateTime?> = dataStore.data
        .map { prefs ->
            prefs[KEY_LAST_SYNC_DATE]?.let { value ->
                ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            }
        }

    suspend fun setLastSyncDate(date: ZonedDateTime) {
        dataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC_DATE] = date.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }
    }

    fun isBackupConfirmed(): Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_CONFIRM_BACKUP] ?: false }

    suspend fun setBackupConfirmed(confirmed: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_CONFIRM_BACKUP] = confirmed }
    }
}