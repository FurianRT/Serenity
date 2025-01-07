package com.furianrt.storage.internal.managers

import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.LocalNote
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.PartImageUri
import com.furianrt.storage.internal.database.notes.entities.PartVideoUri
import com.furianrt.storage.internal.device.AppMediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private class QueueEntry(
    val noteId: String,
    val media: LocalNote.Content.Media,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QueueEntry

        if (noteId != other.noteId) return false
        if (media.name != other.media.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = noteId.hashCode()
        result = 31 * result + media.name.hashCode()
        return result
    }
}

@Singleton
internal class MediaSaver @Inject constructor(
    dispatchers: DispatchersProvider,
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val appMediaSource: AppMediaSource,
) {
    private val scope = CoroutineScope(dispatchers.io + SupervisorJob())
    private val canceledEntries = mutableSetOf<QueueEntry>()
    private val queue = MutableSharedFlow<QueueEntry>(extraBufferCapacity = Int.MAX_VALUE)

    init {
        queue
            .filterNot { entry ->
                isEntryCanceled(entry).also { isCanceled ->
                    if (isCanceled) {
                        removeCanceledEntry(entry)
                    }
                }
            }
            .onEach(::saveMedia)
            .launchIn(scope)
    }

    fun save(noteId: String, media: List<LocalNote.Content.Media>) {
        scope.launch {
            media
                .sortedBy { it is LocalNote.Content.Video }
                .forEach { queue.emit(QueueEntry(noteId, it)) }
        }
    }

    suspend fun cancel(noteId: String, media: LocalNote.Content.Media) {
        if (!isMediaSaved(media)) {
            addCanceledEntry(QueueEntry(noteId, media))
        }
    }

    suspend fun cancel(noteId: String, media: List<LocalNote.Content.Media>) {
        media.forEach { cancel(noteId, it) }
    }

    private suspend fun saveMedia(entry: QueueEntry) {
        if (isMediaSaved(entry.media)) {
            return
        }
        val resultUri = appMediaSource.saveMediaFile(entry.noteId, entry.media) ?: return
        when (entry.media) {
            is LocalNote.Content.Image -> imageDao.update(
                PartImageUri(
                    name = entry.media.name,
                    uri = resultUri,
                    isSaved = true,
                ),
            )

            is LocalNote.Content.Video -> videoDao.update(
                PartVideoUri(
                    name = entry.media.name,
                    uri = resultUri,
                    isSaved = true,
                ),
            )
        }
    }

    private suspend fun isMediaSaved(media: LocalNote.Content.Media): Boolean = when (media) {
        is LocalNote.Content.Image -> imageDao.isSaved(media.name)
        is LocalNote.Content.Video -> videoDao.isSaved(media.name)
    }

    @Synchronized
    private fun isEntryCanceled(entry: QueueEntry): Boolean = canceledEntries.contains(entry)

    @Synchronized
    private fun addCanceledEntry(entry: QueueEntry) {
        canceledEntries.remove(entry)
    }

    @Synchronized
    private fun removeCanceledEntry(entry: QueueEntry) {
        canceledEntries.remove(entry)
    }
}