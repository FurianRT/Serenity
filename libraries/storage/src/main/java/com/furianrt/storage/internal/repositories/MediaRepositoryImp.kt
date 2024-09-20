package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.MediaPermissionStatus
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.PartImageId
import com.furianrt.storage.internal.database.notes.entities.PartVideoId
import com.furianrt.storage.internal.database.notes.mappers.toEntryImage
import com.furianrt.storage.internal.database.notes.mappers.toEntryVideo
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentImage
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentVideo
import com.furianrt.storage.internal.device.AppPrivateMediaSource
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
    private val appPrivateMediaSource: AppPrivateMediaSource,
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

        mediaSaver.save(noteId, media)
    }

    override suspend fun delete(noteId: String, media: List<LocalNote.Content.Media>) {
        withContext(NonCancellable) {
            mediaSaver.cancel(noteId, media)
            val images = media
                .filterIsInstance<LocalNote.Content.Image>()
                .map { PartImageId(it.id) }
            val videos = media
                .filterIsInstance<LocalNote.Content.Video>()
                .map { PartVideoId(it.id) }
            transactionsHelper.startTransaction {
                imageDao.delete(images)
                videoDao.delete(videos)
            }
            appPrivateMediaSource.deleteMediaFile(
                noteId = noteId,
                ids = media.map(LocalNote.Content.Media::id).toSet(),
            )
        }
    }

    override suspend fun deleteMediaFiles(noteId: String) {
        appPrivateMediaSource.deleteAllMediaFiles(noteId)
    }

    override fun getMedia(noteId: String): Flow<List<LocalNote.Content.Media>> = combine(
        imageDao.getImages(noteId).deepMap(EntryNoteImage::toNoteContentImage),
        videoDao.getVideos(noteId).deepMap(EntryNoteVideo::toNoteContentVideo),
    ) { images, videos -> images + videos }

    override suspend fun getDeviceMediaList(): List<DeviceMedia> = sharedMediaSource.getMediaList()

    override fun getMediaPermissionStatus(): MediaPermissionStatus {
        return sharedMediaSource.getMediaPermissionStatus()
    }
}
