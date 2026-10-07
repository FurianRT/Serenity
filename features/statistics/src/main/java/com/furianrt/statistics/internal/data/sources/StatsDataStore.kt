package com.furianrt.statistics.internal.data.sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val KEY_SELECTED_PERIOD = longPreferencesKey("stats_selected_period")

@Singleton
internal class StatsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun getSelectedTimePeriod(): Flow<Long?> = dataStore.data
        .map { prefs -> prefs[KEY_SELECTED_PERIOD] }
        .distinctUntilChanged()

    suspend fun setSelectedTimePeriod(period: Long) {
        dataStore.edit { prefs -> prefs[KEY_SELECTED_PERIOD] = period }
    }
}
