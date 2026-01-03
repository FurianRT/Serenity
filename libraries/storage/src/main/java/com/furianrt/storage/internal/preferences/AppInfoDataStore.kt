package com.furianrt.storage.internal.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val KEY_APP_LAUNCH_COUNT = intPreferencesKey("app_launch_count")
private val KEY_MAIN_ONBOARDING = booleanPreferencesKey("main_onboarding_shown")

@Singleton
internal class AppInfoDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun getAppLaunchCount(): Flow<Int> = dataStore.data
        .map { prefs -> prefs[KEY_APP_LAUNCH_COUNT] ?: 0 }

    suspend fun setAppLaunchCount(count: Int) {
        dataStore.edit { prefs -> prefs[KEY_APP_LAUNCH_COUNT] = count }
    }

    fun isOnboardingShown(): Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_MAIN_ONBOARDING] ?: false }

    suspend fun setOnboardingShown() {
        dataStore.edit { prefs -> prefs[KEY_MAIN_ONBOARDING] = true }
    }
}