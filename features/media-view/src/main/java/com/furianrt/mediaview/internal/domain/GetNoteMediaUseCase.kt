package com.furianrt.mediaview.internal.domain

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetNoteMediaUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(noteId: String): Flow<List<LocalNote.Content.Media>> {
        return notesRepository.getNote(noteId)
            .map { note ->
                note?.content
                    ?.filterIsInstance<LocalNote.Content.MediaBlock>()
                    ?.flatMap(LocalNote.Content.MediaBlock::media)
                    .orEmpty()
            }
    }
}