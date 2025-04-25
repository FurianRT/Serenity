package com.furianrt.domain.usecase

import com.furianrt.domain.TransactionsHelper
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.repositories.TagsRepository
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

    suspend operator fun invoke(noteIds: Set<String>) {
        transactionsHelper.startTransaction {
            notesRepository.deleteNotes(noteIds)
            tagsRepository.deleteUnusedTags()
        }
        noteIds.forEach {  mediaRepository.deleteMediaFiles(it) }
    }
}
