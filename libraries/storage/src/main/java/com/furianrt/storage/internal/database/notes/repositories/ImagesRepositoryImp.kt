package com.furianrt.storage.internal.database.notes.repositories

import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.ImagesRepository
import com.furianrt.storage.internal.database.notes.dao.ContentBlockDao
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.mappers.toEntryContentBlock
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteImage
import javax.inject.Inject

internal class ImagesRepositoryImp @Inject constructor(
    private val blockDao: ContentBlockDao,
    private val imageDao: ImageDao,
    private val transactionsHelper: TransactionsHelper,
) : ImagesRepository {
    override suspend fun upsert(
        noteId: String,
        block: LocalNote.Content.ImagesBlock,
    ) = transactionsHelper.startTransaction {
        blockDao.upsert(block.toEntryContentBlock(noteId))
        block.images.forEach { imageDao.upsert(it.toEntryNoteImage(block.id)) }
    }
}
