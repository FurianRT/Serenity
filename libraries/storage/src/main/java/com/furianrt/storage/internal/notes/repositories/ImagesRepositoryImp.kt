package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.ImagesRepository
import com.furianrt.storage.internal.notes.dao.ContentBlockDao
import com.furianrt.storage.internal.notes.dao.ImageDao
import com.furianrt.storage.internal.notes.mappers.toEntryContentBlock
import com.furianrt.storage.internal.notes.mappers.toEntryNoteImage
import javax.inject.Inject

internal class ImagesRepositoryImp @Inject constructor(
    private val blockDao: ContentBlockDao,
    private val imageDao: ImageDao,
    private val transactionsHelper: TransactionsHelper,
) : ImagesRepository {
    override suspend fun upsert(
        noteId: String,
        block: LocalNote.Content.ImagesBlock,
    ) = transactionsHelper.withTransaction {
        blockDao.upsert(block.toEntryContentBlock(noteId))
        block.images.forEach { imageDao.upsert(it.toEntryNoteImage(block.id)) }
    }
}
