package com.furianrt.settings.internal.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val KEY_APP_RATING = intPreferencesKey("app_rating")

@Singleton
internal class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    fun getAppRating(): Flow<Int> = dataStore.data
        .map { prefs -> prefs[KEY_APP_RATING] ?: 0 }

    suspend fun setAppRating(rating: Int) {
        dataStore.edit { prefs -> prefs[KEY_APP_RATING] = rating }
    }
}