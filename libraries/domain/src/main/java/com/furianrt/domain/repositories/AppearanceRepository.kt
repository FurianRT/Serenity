package com.furianrt.domain.repositories

import com.furianrt.domain.entities.ThemeColor
import kotlinx.coroutines.flow.Flow

interface AppearanceRepository {
    suspend fun updateAppThemeColor(color: ThemeColor)
    fun getAppThemeColor(): Flow<ThemeColor>
    fun getAppThemeColorsList(): List<ThemeColor>
}