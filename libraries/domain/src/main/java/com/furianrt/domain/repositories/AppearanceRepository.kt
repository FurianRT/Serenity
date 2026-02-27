package com.furianrt.domain.repositories

import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AppearanceRepository {
    suspend fun updateAppThemeColor(colorId: String)
    fun getAppThemeColorId(): StateFlow<String?>

    fun getNoteFontColorsList(): List<NoteFontColor>
    fun getNoteFontsList(): List<NoteFontFamily>
    fun getDefaultNoteFont(): Flow<NoteFontFamily?>
    fun getAppFont(): Flow<NoteFontFamily>
    suspend fun setAppFont(font: NoteFontFamily)
    suspend fun setDefaultNoteFont(font: NoteFontFamily?)

    fun getDefaultNoteFontColor(): Flow<NoteFontColor?>
    suspend fun setDefaultNoteFontColor(color: NoteFontColor?)

    fun getDefaultNoteFontSize(): Flow<Int>
    suspend fun setDefaultNoteFontSize(size: Int)

    fun getDefaultNoteMoodId(): Flow<String?>
    suspend fun setDefaultNoteMoodId(moodId: String)

    fun getDefaultNoteBackgroundColorId(): Flow<String?>
    fun getDefaultNoteBackgroundImageId(): Flow<String?>
    suspend fun setDefaultNoteBackgroundColorId(colorId: String?)
    suspend fun setDefaultNoteBackgroundImageId(imageId: String?)

    fun isAutoDetectLocationEnabled(): Flow<Boolean>
    suspend fun setAutoDetectLocationEnabled(enabled: Boolean)
    fun isAutoDetectLocationAsked(): Flow<Boolean>
    suspend fun setAutoDetectLocationAsked(value: Boolean)

    fun isMinimalisticHomeScreenEnabled(): Flow<Boolean>
    suspend fun setMinimalisticHomeScreenEnabled(enabled: Boolean)

    fun isKeepPrevBackgroundEnabled(): Flow<Boolean>
    suspend fun setKeepPrevBackgroundEnabled(enabled: Boolean)
}