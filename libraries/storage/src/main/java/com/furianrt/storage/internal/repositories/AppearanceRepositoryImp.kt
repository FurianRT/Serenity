package com.furianrt.storage.internal.repositories

import com.furianrt.domain.entities.ThemeColor
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.storage.internal.preferences.SerenityDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AppearanceRepositoryImp @Inject constructor(
    private val dataStore: SerenityDataStore,
): AppearanceRepository {

    override suspend fun updateAppThemeColor(color: ThemeColor) {
        dataStore.updateAppThemeColor(color)
    }

    override fun getAppThemeColor(): Flow<ThemeColor> = dataStore.getAppThemeColor()

    override fun getAppThemeColorsList(): List<ThemeColor> = listOf(
        ThemeColor.BLACK,
        ThemeColor.GREEN,
        ThemeColor.BLUE,
    )
}