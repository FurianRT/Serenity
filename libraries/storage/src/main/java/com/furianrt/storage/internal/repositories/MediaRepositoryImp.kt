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
import com.furianrt.storage.internal.device.AppMediaStorage
import com.furianrt.storage.internal.device.DeviceMediaStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class MediaRepositoryImp @Inject constructor(
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val appMediaStorage: AppMediaStorage,
    private val deviceMediaStorage: DeviceMediaStorage,
) : MediaRepository {

    override suspend fun upsert(noteId: String, media: LocalNote.Content.Media) {
        when (val appMedia = appMediaStorage.saveMediaFile(media)) {
            is LocalNote.Content.Image -> {
                imageDao.upsert(appMedia.toEntryImage(noteId))
            }
            is LocalNote.Content.Video -> {
                videoDao.upsert(appMedia.toEntryVideo(noteId))
            }

            null -> Unit
        }
    }

    override suspend fun delete(noteId: String, media: LocalNote.Content.Media) {
        when (media) {
            is LocalNote.Content.Image -> {
                imageDao.delete(media.toEntryImage(noteId))
            }
            is LocalNote.Content.Video -> {
                videoDao.delete(media.toEntryVideo(noteId))
            }
        }
        appMediaStorage.deleteMediaFile(media.id)
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
