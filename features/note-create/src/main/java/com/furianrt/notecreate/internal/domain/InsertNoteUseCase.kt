package com.furianrt.notecreate.internal.domain

import com.furianrt.storage.api.entities.SimpleNote
import com.furianrt.storage.api.repositories.NotesRepository
import javax.inject.Inject

internal class InsertNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    suspend operator fun invoke(note: SimpleNote) {
        notesRepository.insetNote(note)
    }
}