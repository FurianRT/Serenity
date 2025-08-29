package com.furianrt.storage.internal.repositories

import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.storage.internal.preferences.AppearanceDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AppearanceRepositoryImp @Inject constructor(
    private val appearanceDataStore: AppearanceDataStore,
) : AppearanceRepository {

    override suspend fun updateAppThemeColor(colorId: String) {
        appearanceDataStore.updateAppThemeColor(colorId)
    }

    override fun getAppThemeColorId(): Flow<String?> = appearanceDataStore.getAppThemeColorId()

    override fun getNoteFontColorsList(): List<NoteFontColor> = NoteFontColor.entries

    override fun getNoteFontsList(): List<NoteFontFamily> = NoteFontFamily.entries

    override fun getDefaultNoteFont(): Flow<NoteFontFamily?> {
        return appearanceDataStore.getDefaultNoteFont()
    }

    override fun getAppFont(): Flow<NoteFontFamily> = appearanceDataStore.getAppFont()

    override suspend fun setAppFont(font: NoteFontFamily) {
        appearanceDataStore.setAppFont(font)
    }

    override suspend fun setDefaultNoteFont(font: NoteFontFamily?) {
        appearanceDataStore.setDefaultNoteFont(font)
    }

    override fun getDefaultNoteFontColor(): Flow<NoteFontColor?> =
        appearanceDataStore.getDefaultNoteFontColor()

    override suspend fun setDefaultNoteFontColor(color: NoteFontColor?) {
        appearanceDataStore.setDefaultNoteFontColor(color)
    }

    override fun getDefaultNoteFontSize(): Flow<Int> = appearanceDataStore.getDefaultNoteFontSize()

    override suspend fun setDefaultNoteFontSize(size: Int) {
        appearanceDataStore.setDefaultNoteFontSize(size)
    }

    override fun getDefaultNoteMoodId(): Flow<String?> {
        return appearanceDataStore.getDefaultNoteMoodId()
    }

    override suspend fun setDefaultNoteMoodId(moodId: String) {
        appearanceDataStore.setDefaultNoteMoodId(moodId)
    }

    override fun isAutoDetectLocationEnabled(): Flow<Boolean> {
        return appearanceDataStore.isAutoDetectLocationEnabled()
    }

    override suspend fun setAutoDetectLocationEnabled(enabled: Boolean) {
        appearanceDataStore.setAutoDetectLocationEnabled(enabled)
    }

    override fun isAutoDetectLocationAsked(): Flow<Boolean> {
        return appearanceDataStore.isAutoDetectLocationAsked()
    }

    override suspend fun setAutoDetectLocationAsked(value: Boolean) {
        appearanceDataStore.setAutoDetectLocationAsked(value)
    }
}