package com.furianrt.storage.internal.managers

import com.furianrt.domain.entities.CustomSticker
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteCustomBackground
import com.furianrt.storage.BuildConfig
import com.furianrt.storage.internal.database.notes.dao.CustomBackgroundDao
import com.furianrt.storage.internal.database.notes.dao.CustomStickerDao
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.PartCustomStickerUri
import com.furianrt.storage.internal.database.notes.entities.PartImageUri
import com.furianrt.storage.internal.database.notes.entities.PartNoteCustomBackgroundUri
import com.furianrt.storage.internal.database.notes.entities.PartVideoUri
import com.furianrt.storage.internal.database.notes.mappers.toCustomSticker
import com.furianrt.storage.internal.database.notes.mappers.toDomain
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentImage
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentVideo
import com.furianrt.storage.internal.device.AppMediaSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MediaSaver @Inject constructor(
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val customBackgroundDao: CustomBackgroundDao,
    private val customStickerDao: CustomStickerDao,
    private val appMediaSource: AppMediaSource,
) {
    private val canceledEntries = mutableSetOf<String>()
    private val mutex = Mutex()

    suspend fun saveAll() {
        customBackgroundDao.getUnsavedBackgrounds().first().forEach { entry ->
            saveNoteBackground(entry.toDomain())
        }

        customStickerDao.getUnsavedStickers().first().forEach { entry ->
            saveSticker(entry.toCustomSticker())
        }

        videoDao.getUnsavedVideos().first().forEach { entry ->
            saveMedia(noteId = entry.noteId, media = entry.toNoteContentVideo())
        }

        imageDao.getUnsavedImages().first().forEach { entry ->
            saveMedia(noteId = entry.noteId, media = entry.toNoteContentImage())
        }
    }

    suspend fun cancel(media: LocalNote.Content.Media) {
        if (!isMediaSaved(media)) {
            addCanceledEntry(media.id)
        }
    }

    suspend fun cancel(media: List<LocalNote.Content.Media>) {
        media.forEach { cancel(it) }
    }

    suspend fun cancel(background: NoteCustomBackground) {
        if (!isBackgroundSaved(background.id)) {
            addCanceledEntry(background.id)
        }
    }

    suspend fun cancel(sticker: CustomSticker) {
        if (!isStickerSaved(sticker.id)) {
            addCanceledEntry(sticker.id)
        }
    }

    private suspend fun saveMedia(
        noteId: String,
        media: LocalNote.Content.Media,
    ) = mutex.withLock {
        if (isMediaSaved(media)) {
            return@withLock
        }
        if (isEntryCanceled(media.id)) {
            removeCanceledEntry(media.id)
            return@withLock
        }

        val savedMediaData = appMediaSource.saveMediaFile(noteId, media) ?: return@withLock

        if (media.uri.host == BuildConfig.FILE_PROVIDER_AUTHORITY) {
            appMediaSource.deleteFile(media.uri)
        }

        if (isEntryCanceled(media.id)) {
            removeCanceledEntry(media.id)
            appMediaSource.deleteMediaFile(noteId, media)
            return@withLock
        }

        when (media) {
            is LocalNote.Content.Image -> imageDao.update(
                PartImageUri(
                    id = media.id,
                    name = savedMediaData.name,
                    uri = savedMediaData.uri,
                    isSaved = true,
                ),
            )

            is LocalNote.Content.Video -> videoDao.update(
                PartVideoUri(
                    id = media.id,
                    name = savedMediaData.name,
                    uri = savedMediaData.uri,
                    isSaved = true,
                ),
            )
        }
    }

    private suspend fun saveNoteBackground(
        background: NoteCustomBackground,
    ) = mutex.withLock {
        if (isBackgroundSaved(background.id)) {
            return@withLock
        }
        if (isEntryCanceled(background.id)) {
            removeCanceledEntry(background.id)
            return@withLock
        }

        val savedBackgroundData = appMediaSource.saveNoteBackground(background) ?: return@withLock

        if (background.uri.host == BuildConfig.FILE_PROVIDER_AUTHORITY) {
            appMediaSource.deleteFile(background.uri)
        }

        if (isEntryCanceled(background.id)) {
            removeCanceledEntry(background.id)
            appMediaSource.deleteNoteBackgroundFile(background)
            return@withLock
        }

        customBackgroundDao.update(
            PartNoteCustomBackgroundUri(
                id = background.id,
                name = savedBackgroundData.name,
                uri = savedBackgroundData.uri,
                isSaved = true,
            )
        )
    }

    private suspend fun saveSticker(
        sticker: CustomSticker,
    ) = mutex.withLock {
        if (isStickerSaved(sticker.id)) {
            return@withLock
        }
        if (isEntryCanceled(sticker.id)) {
            removeCanceledEntry(sticker.id)
            return@withLock
        }

        val savedStickerData = appMediaSource.saveSticker(sticker) ?: return@withLock

        if (sticker.uri.host == BuildConfig.FILE_PROVIDER_AUTHORITY) {
            appMediaSource.deleteFile(sticker.uri)
        }

        if (isEntryCanceled(sticker.id)) {
            removeCanceledEntry(sticker.id)
            appMediaSource.deleteStickerFile(sticker)
            return@withLock
        }

        customStickerDao.update(
            PartCustomStickerUri(
                id = sticker.id,
                name = savedStickerData.name,
                uri = savedStickerData.uri,
                isSaved = true,
            )
        )
    }

    private suspend fun isMediaSaved(media: LocalNote.Content.Media): Boolean = when (media) {
        is LocalNote.Content.Image -> imageDao.isSaved(media.id)
        is LocalNote.Content.Video -> videoDao.isSaved(media.id)
    }

    private suspend fun isBackgroundSaved(id: String): Boolean = customBackgroundDao.isSaved(id)

    private suspend fun isStickerSaved(id: String): Boolean = customStickerDao.isSaved(id)

    @Synchronized
    private fun isEntryCanceled(id: String): Boolean = canceledEntries.contains(id)

    @Synchronized
    private fun addCanceledEntry(id: String) {
        canceledEntries.add(id)
    }

    @Synchronized
    private fun removeCanceledEntry(id: String) {
        canceledEntries.remove(id)
    }
}