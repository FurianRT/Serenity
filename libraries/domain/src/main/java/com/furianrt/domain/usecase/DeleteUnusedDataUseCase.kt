package com.furianrt.domain.usecase

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.repositories.StickersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteUnusedDataUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val mediaRepository: MediaRepository,
    private val stickersRepository: StickersRepository,
) {
    suspend operator fun invoke() {
        deleteTemplateNotes()
        deleteUnusedHiddenNoteBackgrounds()
        deleteUnusedHiddenCustomStickers()
    }

    private suspend fun deleteTemplateNotes() {
        val templates = notesRepository.getAllTemplates().first()
            .map(SimpleNote::id)
            .toSet()
        deleteNoteUseCase(templates)
    }

    private suspend fun deleteUnusedHiddenNoteBackgrounds() {
        mediaRepository.getHiddenCustomNoteBackgrounds().first().forEach { background ->
            mediaRepository.deleteCustomNoteBackground(background, updateHiddenFlag = false)
        }
    }

    private suspend fun deleteUnusedHiddenCustomStickers() {
        stickersRepository.getHiddenCustomStickers().first().forEach { sticker ->
            stickersRepository.deleteCustomSticker(sticker, updateHiddenFlag = false)
        }
    }
}