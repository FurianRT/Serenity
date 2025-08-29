package com.furianrt.storage.internal.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val KEY_THEME_COLOR = stringPreferencesKey("theme_color")
private val KEY_DEFAULT_NOTE_FONT = stringPreferencesKey("default_note_font")
private val KEY_APP_FONT = stringPreferencesKey("app_font")
private val KEY_DEFAULT_NOTE_FONT_COLOR = stringPreferencesKey("default_note_font_color")
private val KEY_DEFAULT_NOTE_FONT_SIZE = intPreferencesKey("default_note_font_size")
private val KEY_DEFAULT_NOTE_MOOD_ID = stringPreferencesKey("default_note_mood_id")
private val KEY_AUTO_DETECT_LOCATION = booleanPreferencesKey("auto_detect_location")
private val KEY_AUTO_DETECT_LOCATION_ASKED = booleanPreferencesKey("auto_detect_location_asked")

@Singleton
internal class AppearanceDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun getAppThemeColorId(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_THEME_COLOR] }

    suspend fun updateAppThemeColor(colorId: String) {
        dataStore.edit { prefs -> prefs[KEY_THEME_COLOR] = colorId }
    }

    fun getDefaultNoteFont(): Flow<NoteFontFamily?> = dataStore.data
        .map { prefs -> NoteFontFamily.fromString(prefs[KEY_DEFAULT_NOTE_FONT]) }

    suspend fun setDefaultNoteFont(font: NoteFontFamily?) {
        dataStore.edit { prefs ->
            if (font == null) {
                prefs.remove(KEY_DEFAULT_NOTE_FONT)
            } else {
                prefs[KEY_DEFAULT_NOTE_FONT] = font.name
            }
        }
    }

    fun getAppFont(): Flow<NoteFontFamily> = dataStore.data
        .map { prefs ->
            NoteFontFamily.fromString(prefs[KEY_APP_FONT]) ?: NoteFontFamily.NOTO_SANS
        }

    suspend fun setAppFont(font: NoteFontFamily) {
        dataStore.edit { prefs -> prefs[KEY_APP_FONT] = font.name }
    }

    fun getDefaultNoteFontColor(): Flow<NoteFontColor?> = dataStore.data
        .map { prefs -> NoteFontColor.fromString(prefs[KEY_DEFAULT_NOTE_FONT_COLOR]) }

    suspend fun setDefaultNoteFontColor(color: NoteFontColor?) {
        dataStore.edit { prefs ->
            if (color == null) {
                prefs.remove(KEY_DEFAULT_NOTE_FONT_COLOR)
            } else {
                prefs[KEY_DEFAULT_NOTE_FONT_COLOR] = color.name
            }
        }
    }

    fun getDefaultNoteFontSize(): Flow<Int> = dataStore.data
        .map { prefs -> prefs[KEY_DEFAULT_NOTE_FONT_SIZE] ?: 16 }

    suspend fun setDefaultNoteFontSize(size: Int) {
        dataStore.edit { prefs -> prefs[KEY_DEFAULT_NOTE_FONT_SIZE] = size }
    }

    fun getDefaultNoteMoodId(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_DEFAULT_NOTE_MOOD_ID] }

    suspend fun setDefaultNoteMoodId(moodId: String) {
        dataStore.edit { prefs -> prefs[KEY_DEFAULT_NOTE_MOOD_ID] = moodId }
    }

    fun isAutoDetectLocationEnabled(): Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_AUTO_DETECT_LOCATION] ?: false }

    suspend fun setAutoDetectLocationEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_AUTO_DETECT_LOCATION] = enabled }
    }

    fun isAutoDetectLocationAsked(): Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_AUTO_DETECT_LOCATION_ASKED] ?: false }

    suspend fun setAutoDetectLocationAsked(value: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_AUTO_DETECT_LOCATION_ASKED] = value }
    }
}