package com.furianrt.domain

import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val tagsRepository: TagsRepository,
    private val mediaRepository: MediaRepository,
    private val transactionsHelper: TransactionsHelper,
) {
    suspend operator fun invoke(noteId: String) {
        transactionsHelper.startTransaction {
            notesRepository.deleteNote(noteId)
            tagsRepository.deleteUnusedTags()
        }
        mediaRepository.deleteMediaFiles(noteId)
    }
}
