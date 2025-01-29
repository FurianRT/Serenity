package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.storage.internal.database.notes.dao.StickerDao
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.mappers.toEntryIdPart
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteToSticker
import com.furianrt.storage.internal.database.notes.mappers.toEntryTransformationsPart
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentSticker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class StickersRepositoryImp @Inject constructor(
    private val stickerDao: StickerDao,
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
}