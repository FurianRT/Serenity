package com.furianrt.notecreate.internal.domain

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.domain.repositories.NotesRepository
import javax.inject.Inject

internal class InsertNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    suspend operator fun invoke(note: SimpleNote) {
        notesRepository.insetNote(note)
    }
}