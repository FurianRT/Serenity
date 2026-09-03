package com.furianrt.mediasorting.internal.domain

import com.furianrt.core.findInstance
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
        mediaBlockId: String,
    ): Flow<List<LocalNote.Content.Media>> = notesRepository.getNote(noteId).map { note ->
        note?.content
            ?.findInstance<LocalNote.Content.MediaBlock> { it.id == mediaBlockId }
            ?.media
            .orEmpty()
    }
}