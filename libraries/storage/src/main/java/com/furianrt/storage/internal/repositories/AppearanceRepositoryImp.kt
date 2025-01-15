package com.furianrt.storage.internal.repositories

import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.ThemeColor
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.storage.internal.preferences.SerenityDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AppearanceRepositoryImp @Inject constructor(
    private val dataStore: SerenityDataStore,
) : AppearanceRepository {

    override suspend fun updateAppThemeColor(color: ThemeColor) {
        dataStore.updateAppThemeColor(color)
    }

    override fun getAppThemeColor(): Flow<ThemeColor> = dataStore.getAppThemeColor()

    override fun getAppThemeColorsList(): List<ThemeColor> = ThemeColor.entries

    override fun getNoteFontColorsList(): List<NoteFontColor> = NoteFontColor.entries

    override fun getNoteFontsList(): List<NoteFontFamily> = NoteFontFamily.entries

    override fun getDefaultNoteFont(): Flow<NoteFontFamily> = dataStore.getDefaultNoteFont()

    override suspend fun setDefaultNoteFont(font: NoteFontFamily) {
        dataStore.setDefaultNoteFont(font)
    }

    override fun getDefaultNoteFontColor(): Flow<NoteFontColor> = dataStore.getDefaultNoteFontColor()

    override suspend fun setDefaultNoteFontColor(color: NoteFontColor) {
        dataStore.setDefaultNoteFontColor(color)
    }

    override fun getDefaultNoteFontSize(): Flow<Int> = dataStore.getDefaultNoteFontSize()

    override suspend fun setDefaultNoteFontSize(size: Int) {
        dataStore.setDefaultNoteFontSize(size)
    }
}