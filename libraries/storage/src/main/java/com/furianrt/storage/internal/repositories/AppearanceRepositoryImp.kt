package com.furianrt.storage.internal.repositories

import android.content.Context
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.deepMap
import com.furianrt.domain.entities.AppCustomBackground
import com.furianrt.domain.entities.AppTheme
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteTextAlignment
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.storage.internal.database.notes.dao.AppCustomBackgroundDao
import com.furianrt.storage.internal.database.notes.entities.EntryAppCustomBackground
import com.furianrt.storage.internal.database.notes.entities.PartAppCustomBackgroundId
import com.furianrt.storage.internal.database.notes.mappers.toAppCustomBackground
import com.furianrt.storage.internal.database.notes.mappers.toEntry
import com.furianrt.storage.internal.database.notes.mappers.toEntryTextAlignment
import com.furianrt.storage.internal.device.AppMediaSource
import com.furianrt.storage.internal.managers.MediaSaver
import com.furianrt.storage.internal.preferences.AppearanceDataStore
import com.furianrt.storage.internal.workers.SaveMediaWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

internal class AppearanceRepositoryImp @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appearanceDataStore: AppearanceDataStore,
    private val appCustomBackgroundDao: AppCustomBackgroundDao,
    private val appMediaSource: AppMediaSource,
    private val mediaSaver: MediaSaver,
    private val dispatchers: DispatchersProvider,
) : AppearanceRepository {

    private val scope = CoroutineScope(dispatchers.io + SupervisorJob())

    private val appTheme: StateFlow<AppTheme> = combine(
        appearanceDataStore.getAppThemeId(),
        getAppCustomBackgrounds(),
    ) { themeId, customBackgrounds ->
        val custom = customBackgrounds.find { it.id == themeId }
        if (custom != null) {
            AppTheme.Custom(background = custom)
        } else {
            AppTheme.Regular(id = themeId)
        }
    }.flowOn(
        context = dispatchers.io,
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = AppTheme.Regular(id = null),
    )

    override suspend fun updateAppTheme(id: String) {
        appearanceDataStore.updateAppThemeColor(id)
    }

    override fun getAppTheme(): StateFlow<AppTheme> = appTheme

    override fun getAppCustomBackgrounds(): Flow<List<AppCustomBackground>> =
        appCustomBackgroundDao.getAllBackgrounds()
            .map { backgrounds -> backgrounds.sortedByDescending { it.addedDate } }
            .deepMap(EntryAppCustomBackground::toAppCustomBackground)
            .flowOn(dispatchers.io)

    override suspend fun upsertAppCustomBackground(
        background: AppCustomBackground,
        updateFile: Boolean,
    ) {
        appCustomBackgroundDao.upsert(background.toEntry(isSaved = !updateFile))
        if (updateFile) {
            SaveMediaWorker.enqueueOneTime(context)
        }
    }

    override suspend fun deleteAppCustomBackground(background: AppCustomBackground) {
        mediaSaver.cancel(background)
        appCustomBackgroundDao.delete(PartAppCustomBackgroundId(id = background.id))
        appMediaSource.deleteAppBackgroundFile(background)
    }

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

    override suspend fun setDefaultNoteCustomFontColor(color: Int?) {
        appearanceDataStore.setDefaultNoteCustomFontColor(color)
    }

    override suspend fun setDefaultNoteFontSize(size: Int) {
        appearanceDataStore.setDefaultNoteFontSize(size)
    }

    override fun getDefaultNoteMoodId(): Flow<String?> {
        return appearanceDataStore.getDefaultNoteMoodId()
    }

    override suspend fun setDefaultNoteMoodId(moodId: String) {
        appearanceDataStore.setDefaultNoteMoodId(moodId)
    }

    override suspend fun setDefaultNoteBackgroundColorId(colorId: String?) {
        appearanceDataStore.setDefaultNoteBackgroundColorId(colorId)
    }

    override suspend fun setDefaultNoteBackgroundImageId(imageId: String?) {
        appearanceDataStore.setDefaultNoteBackgroundImageId(imageId)
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

    override fun isMinimalisticHomeScreenEnabled(): Flow<Boolean> {
        return appearanceDataStore.isMinimalisticHomeScreenEnabled()
    }

    override suspend fun setMinimalisticHomeScreenEnabled(enabled: Boolean) {
        appearanceDataStore.setMinimalisticHomeScreenEnabled(enabled)
    }

    override fun isKeepPrevBackgroundEnabled(): Flow<Boolean> {
        return appearanceDataStore.isKeepPrevBackgroundEnabled()
    }

    override suspend fun setKeepPrevBackgroundEnabled(enabled: Boolean) {
        appearanceDataStore.setKeepPrevBackgroundEnabled(enabled)
    }

    override fun isKeepPrevTextAlignEnabled(): Flow<Boolean> {
        return appearanceDataStore.isKeepPrevTextAlignEnabled()
    }

    override suspend fun setKeepPrevTextAlignEnabled(enabled: Boolean) {
        appearanceDataStore.setKeepPrevTextAlignEnabled(enabled)
    }

    override fun isKeepPrevLineHeightEnabled(): Flow<Boolean> {
        return appearanceDataStore.isKeepPrevLineHeightEnabled()
    }

    override suspend fun setKeepPrevLineHeightEnabled(enabled: Boolean) {
        appearanceDataStore.setKeepPrevLineHeightEnabled(enabled)
    }

    override suspend fun setDefaultNoteLineHeight(multiplier: Float) {
        appearanceDataStore.setDefaultNoteLineHeight(multiplier)
    }

    override suspend fun setDefaultNoteTextAlign(textAlignment: NoteTextAlignment) {
        appearanceDataStore.setDefaultNoteTextAlign(textAlignment.toEntryTextAlignment())
    }
}