package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.MediaPermissionStatus
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.mappers.toEntryImage
import com.furianrt.storage.internal.database.notes.mappers.toEntryVideo
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentImage
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentVideo
import com.furianrt.storage.internal.device.DeviceMediaStorage
import com.furianrt.storage.internal.managers.MediaSaver
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaRepositoryImp @Inject constructor(
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val deviceMediaStorage: DeviceMediaStorage,
    private val mediaSaver: MediaSaver,
) : MediaRepository {

    override suspend fun upsert(
        noteId: String,
        media: List<LocalNote.Content.Media>,
    ) = withContext(NonCancellable) {
        val images = media
            .filterIsInstance<LocalNote.Content.Image>()
            .map { it.toEntryImage(noteId) }
        if (images.isNotEmpty()) {
            imageDao.upsert(images)
        }

        val videos = media
            .filterIsInstance<LocalNote.Content.Video>()
            .map { it.toEntryVideo(noteId) }
        if (videos.isNotEmpty()) {
            videoDao.upsert(videos)
        }

        mediaSaver.save(noteId, media)
    }

    override suspend fun delete(noteId: String, media: LocalNote.Content.Media) {
        mediaSaver.cancel(noteId, media)
        when (media) {
            is LocalNote.Content.Image -> {
                imageDao.delete(media.toEntryImage(noteId))
            }

            is LocalNote.Content.Video -> {
                videoDao.delete(media.toEntryVideo(noteId))
            }
        }
    }

    override fun getMedia(noteId: String): Flow<List<LocalNote.Content.Media>> = combine(
        imageDao.getImages(noteId).deepMap(EntryNoteImage::toNoteContentImage),
        videoDao.getVideos(noteId).deepMap(EntryNoteVideo::toNoteContentVideo)
    ) { images, videos -> images + videos }

    override suspend fun getDeviceMediaList(): List<DeviceMedia> = deviceMediaStorage.getMediaList()

    override fun getMediaPermissionStatus(): MediaPermissionStatus {
        return deviceMediaStorage.getMediaPermissionStatus()
    }
}
