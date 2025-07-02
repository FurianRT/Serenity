package com.furianrt.mediasorting.internal.domain

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.NotesRepository
import javax.inject.Inject

internal class GetNoteMediaUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(
        noteId: String,
        mediaBlockId: String,
    ): List<LocalNote.Content.Media> = notesRepository.getNoteContentFromCache(noteId)
        .filterIsInstance<LocalNote.Content.MediaBlock>()
        .find { it.id == mediaBlockId }
        ?.media
        .orEmpty()
}