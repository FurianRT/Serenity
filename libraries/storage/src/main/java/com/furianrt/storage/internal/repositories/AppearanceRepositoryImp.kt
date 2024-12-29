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

    override fun getAppThemeColorsList(): List<ThemeColor> = listOf(
        ThemeColor.BLACK,
        ThemeColor.GREEN,
        ThemeColor.PURPLE,
        ThemeColor.PURPLE_DARK,
    )

    override fun getNoteFontColorsList(): List<NoteFontColor> = listOf(
        NoteFontColor.WHITE,
        NoteFontColor.GREY_LIGHT,
        NoteFontColor.GREY,
        NoteFontColor.GREY_DARK,
        NoteFontColor.PINK_LIGHT,
        NoteFontColor.PINK,
        NoteFontColor.PINK_DARK,
        NoteFontColor.YELLOW_LIGHT,
        NoteFontColor.YELLOW,
        NoteFontColor.YELLOW_DARK,
        NoteFontColor.GREEN_LIGHT,
        NoteFontColor.GREEN,
        NoteFontColor.GREEN_DARK,
        NoteFontColor.BLUE_LIGHT,
        NoteFontColor.BLUE,
        NoteFontColor.BLUE_DARK,
        NoteFontColor.PURPLE_LIGHT,
        NoteFontColor.PURPLE,
        NoteFontColor.PURPLE_DARK,
        NoteFontColor.RED_LIGHT,
        NoteFontColor.RED,
        NoteFontColor.RED_DARK,
    )

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