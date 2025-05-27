package com.furianrt.domain.usecase

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.domain.repositories.NotesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteTemplateNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) {
    suspend operator fun invoke() {
        val templates = notesRepository.getAllTemplates().first()
            .map(SimpleNote::id)
            .toSet()
        deleteNoteUseCase(templates)
    }
}