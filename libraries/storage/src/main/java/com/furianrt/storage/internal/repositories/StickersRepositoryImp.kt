package com.furianrt.storage.internal.repositories

import android.content.Context
import com.furianrt.core.deepMap
import com.furianrt.domain.entities.CustomSticker
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.storage.internal.database.notes.dao.CustomStickerDao
import com.furianrt.storage.internal.database.notes.dao.StickerDao
import com.furianrt.storage.internal.database.notes.entities.EntryCustomSticker
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.entities.PartCustomStickerId
import com.furianrt.storage.internal.database.notes.entities.PartCustomStickerIsHidden
import com.furianrt.storage.internal.database.notes.mappers.toCustomSticker
import com.furianrt.storage.internal.database.notes.mappers.toEntryCustomSticker
import com.furianrt.storage.internal.database.notes.mappers.toEntryIdPart
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteToSticker
import com.furianrt.storage.internal.database.notes.mappers.toEntryTransformationsPart
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentSticker
import com.furianrt.storage.internal.device.AppMediaSource
import com.furianrt.storage.internal.managers.MediaSaver
import com.furianrt.storage.internal.workers.SaveMediaWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class StickersRepositoryImp @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val stickerDao: StickerDao,
    private val customStickerDao: CustomStickerDao,
    private val appMediaSource: AppMediaSource,
    private val mediaSaver: MediaSaver,
) : StickersRepository {
    override suspend fun insert(noteId: String, stickers: List<LocalNote.Sticker>) {
        stickerDao.insert(stickers.map { it.toEntryNoteToSticker(noteId) })
    }

    override suspend fun update(stickers: List<LocalNote.Sticker>) {
        stickerDao.update(stickers.map(LocalNote.Sticker::toEntryTransformationsPart))
    }

    override suspend fun delete(stickers: List<LocalNote.Sticker>) {
        stickerDao.delete(stickers.map(LocalNote.Sticker::toEntryIdPart))
    }

    override fun getStickers(noteId: String): Flow<List<LocalNote.Sticker>> {
        return stickerDao.getStickers(noteId)
            .deepMap(EntryNoteSticker::toNoteContentSticker)
    }

    override suspend fun upsertCustomStickers(
        stickers: List<CustomSticker>,
        updateFile: Boolean,
    ) {
        customStickerDao.upsert(stickers.map { it.toEntryCustomSticker(isSaved = !updateFile) })
        if (updateFile) {
            SaveMediaWorker.enqueueOneTime(context)
        }
    }

    override suspend fun deleteCustomSticker(
        sticker: CustomSticker,
        updateHiddenFlag: Boolean,
    ) {
        if (stickerDao.hasStickerWithTypeId(sticker.id)) {
            if (updateHiddenFlag) {
                customStickerDao.update(
                    PartCustomStickerIsHidden(
                        id = sticker.id,
                        isHidden = true,
                    )
                )
            }
        } else {
            mediaSaver.cancel(sticker)
            customStickerDao.delete(PartCustomStickerId(id = sticker.id))
            appMediaSource.deleteStickerFile(sticker)
        }
    }

    override fun getNotHiddenCustomStickers(): Flow<List<CustomSticker>> {
        return customStickerDao.getNotHiddenStickers()
            .deepMap(EntryCustomSticker::toCustomSticker)
            .map { list -> list.sortedByDescending { it.addedDate } }
    }


    override fun getHiddenCustomStickers(): Flow<List<CustomSticker>> {
        return customStickerDao.getHiddenStickers()
            .deepMap(EntryCustomSticker::toCustomSticker)
    }

    override fun getAllCustomStickers(): Flow<List<CustomSticker>> {
        return customStickerDao.getAllStickers().deepMap(EntryCustomSticker::toCustomSticker)
    }
}