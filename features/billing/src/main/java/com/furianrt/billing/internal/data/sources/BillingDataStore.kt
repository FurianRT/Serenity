package com.furianrt.billing.internal.data.sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val KEY_HAS_SERENITY_PLUS = booleanPreferencesKey("has_serenity_plus")

@Singleton
internal class BillingDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun isSerenityPlusAvailable(): Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_HAS_SERENITY_PLUS] ?: false }
        .distinctUntilChanged()

    suspend fun setSerenityPlusAvailable(available: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_HAS_SERENITY_PLUS] = available }
    }
}
