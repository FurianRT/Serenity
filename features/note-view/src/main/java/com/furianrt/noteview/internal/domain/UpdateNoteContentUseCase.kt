package com.furianrt.noteview.internal.domain

import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UpdateNoteContentUseCase @Inject constructor(
    private val transactionsHelper: TransactionsHelper,
    private val notesRepository: NotesRepository,
    private val tagsRepository: TagsRepository,
    private val mediaRepository: MediaRepository,
) {
    suspend operator fun invoke(
        noteId: String,
        content: List<LocalNote.Content>,
        tags: List<LocalNote.Tag>,
    ) = withContext(NonCancellable) {
        transactionsHelper.startTransaction {
            mediaRepository.upsert(
                noteId = noteId,
                media = content
                    .filterIsInstance<LocalNote.Content.MediaBlock>()
                    .flatMap(LocalNote.Content.MediaBlock::media),
            )
            notesRepository.updateNoteText(noteId, content)
            tagsRepository.upsert(noteId, tags)
        }
    }
}