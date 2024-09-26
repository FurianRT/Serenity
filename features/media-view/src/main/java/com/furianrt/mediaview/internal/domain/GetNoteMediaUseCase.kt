package com.furianrt.mediaview.internal.domain

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.NotesRepository
import javax.inject.Inject

internal class GetNoteMediaUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(noteId: String): List<LocalNote.Content.Media> {
        return notesRepository.getNoteContentFromCache(noteId)
            .filterIsInstance<LocalNote.Content.MediaBlock>()
            .flatMap(LocalNote.Content.MediaBlock::media)
    }
}