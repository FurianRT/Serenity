package com.furianrt.domain.repositories

import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import kotlinx.coroutines.flow.Flow

interface AppearanceRepository {
    suspend fun updateAppThemeColor(colorId: String)
    fun getAppThemeColorId(): Flow<String?>
    fun getNoteFontColorsList(): List<NoteFontColor>
    fun getNoteFontsList(): List<NoteFontFamily>
    fun getDefaultNoteFont(): Flow<NoteFontFamily>
    suspend fun setDefaultNoteFont(font: NoteFontFamily)
    fun getDefaultNoteFontColor(): Flow<NoteFontColor>
    suspend fun setDefaultNoteFontColor(color: NoteFontColor)
    fun getDefaultNoteFontSize(): Flow<Int>
    suspend fun setDefaultNoteFontSize(size: Int)
}