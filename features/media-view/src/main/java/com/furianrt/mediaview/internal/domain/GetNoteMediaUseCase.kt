package com.furianrt.mediaview.internal.domain

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetNoteMediaUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(
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
        }
    }
}