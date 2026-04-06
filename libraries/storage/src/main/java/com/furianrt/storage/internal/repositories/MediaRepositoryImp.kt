package com.furianrt.storage.internal.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.deepMap
import com.furianrt.domain.TransactionsHelper
import com.furianrt.domain.entities.DeviceAlbum
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.entities.LocalMedia
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteCustomBackground
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.storage.internal.database.notes.dao.CustomBackgroundDao
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.dao.VoiceDao
import com.furianrt.storage.internal.database.notes.entities.EntryNoteCustomBackground
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVoice
import com.furianrt.storage.internal.database.notes.entities.PartImageId
import com.furianrt.storage.internal.database.notes.entities.PartNoteCustomBackgroundId
import com.furianrt.storage.internal.database.notes.entities.PartNoteCustomBackgroundIsHidden
import com.furianrt.storage.internal.database.notes.entities.PartVideoId
import com.furianrt.storage.internal.database.notes.entities.PartVoiceId
import com.furianrt.storage.internal.database.notes.mappers.toDomain
import com.furianrt.storage.internal.database.notes.mappers.toEntry
import com.furianrt.storage.internal.database.notes.mappers.toEntryImage
import com.furianrt.storage.internal.database.notes.mappers.toEntryVideo
import com.furianrt.storage.internal.database.notes.mappers.toEntryVoice
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentImage
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentVideo
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentVoice
import com.furianrt.storage.internal.device.AppMediaSource
import com.furianrt.storage.internal.device.SharedMediaSource
import com.furianrt.storage.internal.managers.MediaSaver
import com.furianrt.storage.internal.workers.SaveMediaWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class MediaRepositoryImp @Inject constructor(
    private val noteDao: NoteDao,
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val voiceDao: VoiceDao,
    private val customBackgroundDao: CustomBackgroundDao,
    private val sharedMediaSource: SharedMediaSource,
    private val appMediaSource: AppMediaSource,
    private val mediaSaver: MediaSaver,
    private val transactionsHelper: TransactionsHelper,
    private val dispatchers: DispatchersProvider,
    @param:ApplicationContext private val context: Context,
) : MediaRepository {

    override suspend fun insertMedia(
        noteId: String,
        media: List<LocalNote.Content.Media>,
        updateFile: Boolean,
    ) = withContext(NonCancellable) {
        val images = media
            .filterIsInstance<LocalNote.Content.Image>()
            .map { it.toEntryImage(noteId = noteId, isSaved = !updateFile) }
        if (images.isNotEmpty()) {
            imageDao.insert(images)
        }

        val videos = media
            .filterIsInstance<LocalNote.Content.Video>()
            .map { it.toEntryVideo(noteId = noteId, isSaved = !updateFile) }
        if (videos.isNotEmpty()) {
            videoDao.insert(videos)
        }

        if (media.isNotEmpty() && updateFile) {
            SaveMediaWorker.enqueueOneTime(context)
        }
    }

    override suspend fun deleteMedia(noteId: String, media: List<LocalNote.Content.Media>) {
        if (media.isEmpty()) {
            return
        }
        withContext(NonCancellable) {
            mediaSaver.cancel(media)
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
            appMediaSource.deleteMediaFile(
                noteId = noteId,
                media = media.toSet(),
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

    override fun getAllMedia(): Flow<List<LocalNote.Content.Media>> = combine(
        imageDao.getAllImages().deepMap(EntryNoteImage::toNoteContentImage),
        videoDao.getAllVideos().deepMap(EntryNoteVideo::toNoteContentVideo),
    ) { images, videos -> images + videos }

    override suspend fun getDeviceMediaList(
        allowVideo: Boolean,
        albumId: String?,
    ): List<DeviceMedia> = sharedMediaSource.getMediaList(allowVideo, albumId)

    override suspend fun getDeviceAlbumsList(allowVideo: Boolean): List<DeviceAlbum> {
        return sharedMediaSource.getAlbumsList(allowVideo)
    }

    override suspend fun saveToGallery(media: LocalMedia): Boolean {
        return sharedMediaSource.saveToGallery(media)
    }

    override suspend fun insertVoice(noteId: String, voices: List<LocalNote.Content.Voice>) {
        voiceDao.insert(voices.map { it.toEntryVoice(noteId) })
    }

    override suspend fun deleteVoice(noteId: String, voices: List<LocalNote.Content.Voice>) {
        if (voices.isEmpty()) {
            return
        }
        withContext(NonCancellable) {
            transactionsHelper.startTransaction {
                voiceDao.delete(voices.map { PartVoiceId(it.id) })
            }
            appMediaSource.deleteVoiceFile(
                noteId = noteId,
                voiceIds = voices.map(LocalNote.Content.Voice::id).toSet(),
            )
        }
    }

    override fun getVoices(noteId: String): Flow<List<LocalNote.Content.Voice>> {
        return voiceDao.getVoices(noteId).deepMap(EntryNoteVoice::toNoteContentVoice)
    }

    override fun getAllVoices(): Flow<List<LocalNote.Content.Voice>> {
        return voiceDao.getAllVoices().deepMap(EntryNoteVoice::toNoteContentVoice)
    }

    override suspend fun createMediaDestinationFile(
        noteId: String,
        mediaId: String,
        mediaName: String,
    ): File? {
        return appMediaSource.createMediaFile(noteId, mediaId, mediaName)
    }

    override suspend fun createNoteBackgroundDestinationFile(
        id: String,
        name: String,
    ): File? = appMediaSource.createNoteBackgroundFile(id, name)

    override suspend fun deleteFile(file: File) {
        appMediaSource.deleteFile(file)
    }

    override suspend fun createVoiceDestinationFile(noteId: String, voiceId: String): File? {
        return appMediaSource.createVoiceFile(noteId, voiceId)
    }

    override suspend fun deleteVoiceFile(noteId: String, voiceId: String): Boolean {
        return appMediaSource.deleteVoiceFile(noteId, voiceId)
    }

    override fun getRelativeUri(file: File): Uri = appMediaSource.getRelativeUri(file)

    override suspend fun getAspectRatio(file: File): Float {
        return appMediaSource.getAspectRatio(file)
    }

    override suspend fun loadBitmapFromUri(uri: Uri): Bitmap = withContext(dispatchers.io) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return@withContext ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.setTargetSize(256, 256)
        }
    }

    override suspend fun upsertCustomNoteBackground(
        background: NoteCustomBackground,
        updateFile: Boolean,
    ) {
        customBackgroundDao.upsert(background.toEntry(isSaved = !updateFile))
        if (updateFile) {
            SaveMediaWorker.enqueueOneTime(context)
        }
    }

    override suspend fun deleteCustomNoteBackground(
        background: NoteCustomBackground,
        updateHiddenFlag: Boolean,
    ) {
        if (noteDao.hasNoteWithBackgroundId(background.id)) {
            if (updateHiddenFlag) {
                customBackgroundDao.update(
                    PartNoteCustomBackgroundIsHidden(
                        id = background.id,
                        isHidden = true,
                    )
                )
            }
        } else {
            mediaSaver.cancel(background)
            customBackgroundDao.delete(PartNoteCustomBackgroundId(id = background.id))
            appMediaSource.deleteNoteBackgroundFile(background)
        }
    }

    override fun getNotHiddenCustomNoteBackgrounds(): Flow<List<NoteCustomBackground>> {
        return customBackgroundDao.getNotHiddenBackgrounds()
            .deepMap(EntryNoteCustomBackground::toDomain)
            .map { list -> list.sortedByDescending { it.addedDate } }
    }

    override fun getHiddenCustomNoteBackgrounds(): Flow<List<NoteCustomBackground>> {
        return customBackgroundDao.getHiddenBackgrounds()
            .deepMap(EntryNoteCustomBackground::toDomain)
    }

    override fun getAllCustomNoteBackgrounds(): Flow<List<NoteCustomBackground>> {
        return customBackgroundDao.getAllBackgrounds().deepMap(EntryNoteCustomBackground::toDomain)
    }

    override suspend fun saveAllMedia() {
        mediaSaver.saveAll()
    }
}
