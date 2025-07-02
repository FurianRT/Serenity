package com.furianrt.mediaview.internal.domain

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.NotesRepository
import javax.inject.Inject

internal class GetNoteMediaUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(noteId: String, blockId: String?): List<LocalNote.Content.Media> {
        return notesRepository.getNoteContentFromCache(noteId)
            .filterIsInstance<LocalNote.Content.MediaBlock>()
            .filter { blockId == null || it.id == blockId }
            .flatMap(LocalNote.Content.MediaBlock::media)
    }
}