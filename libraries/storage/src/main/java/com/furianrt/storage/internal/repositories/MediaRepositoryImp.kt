package com.furianrt.storage.internal.repositories

import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.MediaPermissionStatus
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.mappers.toEntryImage
import com.furianrt.storage.internal.database.notes.mappers.toEntryVideo
import com.furianrt.storage.internal.device.AppMediaStorage
import com.furianrt.storage.internal.device.DeviceMediaStorage
import com.furianrt.storage.internal.managers.MediaSaver
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaRepositoryImp @Inject constructor(
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val deviceMediaStorage: DeviceMediaStorage,
    private val appMediaStorage: AppMediaStorage,
    private val mediaSaver: MediaSaver,
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

    override suspend fun delete(noteId: String, media: LocalNote.Content.Media) {
        withContext(NonCancellable) {
            mediaSaver.cancel(noteId, media)
            when (media) {
                is LocalNote.Content.Image -> imageDao.delete(media.id)
                is LocalNote.Content.Video -> videoDao.delete(media.id)
            }
            appMediaStorage.deleteMediaFile(noteId, media.id)
        }
    }

    override suspend fun deleteMediaFiles(noteId: String) {
        appMediaStorage.deleteAllMediaFiles(noteId)
    }

    override suspend fun getDeviceMediaList(): List<DeviceMedia> = deviceMediaStorage.getMediaList()

    override fun getMediaPermissionStatus(): MediaPermissionStatus {
        return deviceMediaStorage.getMediaPermissionStatus()
    }
}
