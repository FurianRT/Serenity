package com.furianrt.backup.internal.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val KEY_GOOGLE_ACCESS_TOKEN = stringPreferencesKey("google_access_token")
private val KEY_AUTO_BACKUP = booleanPreferencesKey("auto_backup")

@ViewModelScoped
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
}