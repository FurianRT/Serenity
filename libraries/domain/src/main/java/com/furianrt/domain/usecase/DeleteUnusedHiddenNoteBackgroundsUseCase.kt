package com.furianrt.domain.usecase

import com.furianrt.domain.repositories.MediaRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteUnusedHiddenNoteBackgroundsUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
) {
    suspend operator fun invoke() {
        mediaRepository.getHiddenCustomNoteBackgrounds().first().forEach { background ->
            mediaRepository.deleteCustomNoteBackground(background, updateHiddenFlag = false)
        }
    }
}