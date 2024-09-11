package com.furianrt.storage.internal.managers

import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.PartImageUri
import com.furianrt.storage.internal.database.notes.entities.PartVideoUri
import com.furianrt.storage.internal.device.AppMediaStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        if (media.id != other.media.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = noteId.hashCode()
        result = 31 * result + media.id.hashCode()
        return result
    }
}

@Singleton
internal class MediaSaver @Inject constructor(
    dispatchers: DispatchersProvider,
    private val imageDao: ImageDao,
    private val videoDao: VideoDao,
    private val appMediaStorage: AppMediaStorage,
) {
    private val canceledEntries = mutableSetOf<QueueEntry>()
    private val scope = CoroutineScope(dispatchers.io + SupervisorJob())
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
        media
            .sortedBy { it is LocalNote.Content.Video }
            .forEach { queue.tryEmit(QueueEntry(noteId, it)) }
    }

    suspend fun cancel(noteId: String, media: LocalNote.Content.Media) {
        if (!isMediaSaved(media)) {
            addCanceledEntry(QueueEntry(noteId, media))
        }
    }

    private suspend fun saveMedia(entry: QueueEntry) {
        if (isMediaSaved(entry.media)) {
            return
        }
        val resultUri = appMediaStorage.saveMediaFile(entry.noteId, entry.media) ?: return
        when (entry.media) {
            is LocalNote.Content.Image -> imageDao.update(
                PartImageUri(
                    id = entry.media.id,
                    uri = resultUri,
                    isSaved = true,
                ),
            )

            is LocalNote.Content.Video -> videoDao.update(
                PartVideoUri(
                    id = entry.media.id,
                    uri = resultUri,
                    isSaved = true,
                ),
            )
        }
    }

    private suspend fun isMediaSaved(media: LocalNote.Content.Media): Boolean = when (media) {
        is LocalNote.Content.Image -> imageDao.isSaved(media.id)
        is LocalNote.Content.Video -> videoDao.isSaved(media.id)
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