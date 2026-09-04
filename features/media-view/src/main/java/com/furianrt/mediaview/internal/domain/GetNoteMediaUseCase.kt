package com.furianrt.mediaview.internal.domain

import com.furianrt.core.DispatchersProvider
import com.furianrt.core.deepFilter
import com.furianrt.core.deepMap
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteMedia
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class GetNoteMediaUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val notesRepository: NotesRepository,
    private val mediaRepository: MediaRepository,
) {
    operator fun invoke(
        noteId: String?,
        blockId: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Flow<List<LocalNote.Content.Media>> = if (noteId != null) {
        getMediaForNote(
            noteId = noteId,
            blockId = blockId,
        )
    } else {
        getAllMedia(
            startDate = startDate,
            endDate = endDate,
        )
    }

    private fun getMediaForNote(
        noteId: String,
        blockId: String?,
    ): Flow<List<LocalNote.Content.Media>> {
        val mediaBlocks = notesRepository.getNote(noteId).map { note ->
            note?.content?.filterIsInstance<LocalNote.Content.MediaBlock>().orEmpty()
        }
        return if (blockId != null) {
            mediaBlocks.map { blocks -> blocks.find { it.id == blockId }?.media.orEmpty() }
        } else {
            mediaBlocks.map { it.flatMap(LocalNote.Content.MediaBlock::media) }
        }.flowOn(dispatchers.default)
    }

    private fun getAllMedia(
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Flow<List<LocalNote.Content.Media>> = if (startDate != null) {
        mediaRepository.getNotesMedia().deepFilter { media ->
            val noteDate = media.noteDate.toLocalDate()
            if (endDate != null) {
                noteDate in startDate..endDate
            } else {
                noteDate == startDate
            }
        }
    } else {
        mediaRepository.getNotesMedia()
    }
        .deepMap(NoteMedia::toLocalNoteMedia)
        .flowOn(dispatchers.default)
}

private fun NoteMedia.toLocalNoteMedia(): LocalNote.Content.Media = when (this) {
    is NoteMedia.Image -> LocalNote.Content.Image(
        id = id,
        name = name,
        ratio = ratio,
        uri = uri,
        addedDate = addedDate,
    )

    is NoteMedia.Video -> LocalNote.Content.Video(
        id = id,
        name = name,
        ratio = ratio,
        uri = uri,
        addedDate = addedDate,
        duration = duration,
    )
}
