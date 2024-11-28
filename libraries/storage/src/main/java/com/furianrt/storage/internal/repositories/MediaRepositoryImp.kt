package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.TransactionsHelper
import com.furianrt.domain.entities.LocalMedia
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.PartImageName
import com.furianrt.storage.internal.database.notes.entities.PartVideoName
import com.furianrt.storage.internal.database.notes.mappers.toEntryImage
import com.furianrt.storage.internal.database.notes.mappers.toEntryVideo
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentImage
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentVideo
import com.furianrt.storage.internal.device.AppMediaSource
import com.furianrt.storage.internal.device.SharedMediaSource
import com.furianrt.storage.internal.managers.MediaSaver
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaRepositoryImp @Inject constructor(
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val sharedMediaSource: SharedMediaSource,
    private val appMediaSource: AppMediaSource,
    private val mediaSaver: MediaSaver,
    private val transactionsHelper: TransactionsHelper,
) : MediaRepository {

    override suspend fun insert(
        noteId: String,
        media: List<LocalNote.Content.Media>,
    ) = withContext(NonCancellable) {
        val images = media
            .filterIsInstance<LocalNote.Content.Image>()
            .map { it.toEntryImage(noteId = noteId, isSaved = false) }
        if (images.isNotEmpty()) {
            imageDao.insert(images)
        }

        val videos = media
            .filterIsInstance<LocalNote.Content.Video>()
            .map { it.toEntryVideo(noteId = noteId, isSaved = false) }
        if (videos.isNotEmpty()) {
            videoDao.insert(videos)
        }

        if (media.isNotEmpty()) {
            mediaSaver.save(noteId, media)
        }
    }

    override suspend fun delete(noteId: String, media: List<LocalNote.Content.Media>) {
        if (media.isEmpty()) {
            return
        }
        withContext(NonCancellable) {
            mediaSaver.cancel(noteId, media)
            val images = media
                .filterIsInstance<LocalNote.Content.Image>()
                .map { PartImageName(it.name) }
            val videos = media
                .filterIsInstance<LocalNote.Content.Video>()
                .map { PartVideoName(it.name) }
            transactionsHelper.startTransaction {
                imageDao.delete(images)
                videoDao.delete(videos)
            }
            appMediaSource.deleteMediaFile(
                noteId = noteId,
                names = media.map(LocalNote.Content.Media::name).toSet(),
            )
        }
    }

    override suspend fun deleteMediaFiles(noteId: String) {
        appMediaSource.deleteAllMediaFiles(noteId)
    }

    override fun getMedia(noteId: String): Flow<List<LocalNote.Content.Media>> = combine(
        imageDao.getImages(noteId).deepMap(EntryNoteImage::toNoteContentImage),
        videoDao.getVideos(noteId).deepMap(EntryNoteVideo::toNoteContentVideo),
    ) { images, videos -> images + videos }

    override suspend fun getDeviceMediaList(): List<DeviceMedia> = sharedMediaSource.getMediaList()

    override suspend fun saveToGallery(media: LocalMedia): Boolean {
        return sharedMediaSource.saveToGallery(media)
    }
}
