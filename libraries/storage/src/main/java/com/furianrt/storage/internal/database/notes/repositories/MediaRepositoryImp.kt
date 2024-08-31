package com.furianrt.storage.internal.database.notes.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.internal.database.TransactionsHelper
import com.furianrt.storage.internal.database.notes.dao.ContentBlockDao
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.mappers.toEntryContentBlock
import com.furianrt.storage.internal.database.notes.mappers.toEntryImage
import com.furianrt.storage.internal.database.notes.mappers.toEntryVideo
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentImage
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentVideo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class MediaRepositoryImp @Inject constructor(
    private val blockDao: ContentBlockDao,
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val appMediaRepository: AppMediaRepository,
    private val transactionsHelper: TransactionsHelper,
) : MediaRepository {
    override suspend fun upsert(
        noteId: String,
        block: LocalNote.Content.MediaBlock,
    ) = transactionsHelper.startTransaction {
        blockDao.upsert(block.toEntryContentBlock(noteId))
        block.media.forEach { item ->
            appMediaRepository.saveMediaFile(item)
            upsertMedia(media = item, blockId = block.id)
        }
    }

    override suspend fun delete(
        blockId: String,
        media: LocalNote.Content.Media,
    ) = transactionsHelper.startTransaction {
        when (media) {
            is LocalNote.Content.Image -> imageDao.delete(media.toEntryImage(blockId))
            is LocalNote.Content.Video -> videoDao.delete(media.toEntryVideo(blockId))
        }
        val images = imageDao.getImages(blockId).first()
        val videos = videoDao.getVideos(blockId).first()
        if (images.isEmpty() && videos.isEmpty()) {
            blockDao.delete(blockId)
        }
    }

    override suspend fun getMedia(noteId: String): List<LocalNote.Content.Media> {
        val result = mutableListOf<LocalNote.Content.Media>()
        val blocks = blockDao.getBlocks(noteId).first()
        blocks.forEach { block ->
            val images = imageDao.getImages(block.id).first().map { it.toNoteContentImage() }
            val videos = videoDao.getVideos(block.id).first().map { it.toNoteContentVideo() }
            val blockMedia = (images + videos).sortedBy(LocalNote.Content.Media::position)
            result.addAll(blockMedia)
        }
        return result
    }

    private suspend fun upsertMedia(media: LocalNote.Content.Media, blockId: String) {
        when (media) {
            is LocalNote.Content.Image -> imageDao.upsert(media.toEntryImage(blockId = blockId))
            is LocalNote.Content.Video -> videoDao.upsert(media.toEntryVideo(blockId = blockId))
        }
    }
}
