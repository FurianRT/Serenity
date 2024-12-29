package com.furianrt.notepage.internal.domian

import com.furianrt.core.hasItem
import com.furianrt.domain.TransactionsHelper
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.repositories.TagsRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
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
        fontFamily: NoteFontFamily,
        fontColor: NoteFontColor,
        fontSize: Int,
    ) = withContext(NonCancellable) {
        val newMedia = content
            .filterIsInstance<LocalNote.Content.MediaBlock>()
            .flatMap(LocalNote.Content.MediaBlock::media)

        transactionsHelper.startTransaction {
            val mediaToDelete = mediaRepository.getMedia(noteId)
                .first()
                .filterNot { media -> newMedia.hasItem { it.name == media.name } }

            val mediaToInsert = content
                .filterIsInstance<LocalNote.Content.MediaBlock>()
                .flatMap(LocalNote.Content.MediaBlock::media)

            val tagsToDelete = tagsRepository.getTags(noteId)
                .first()
                .filterNot { tag -> tags.hasItem { tag.title == it.title } }

            if (mediaToInsert.isNotEmpty()) {
                mediaRepository.insert(noteId, mediaToInsert)
            }

            if (mediaToDelete.isNotEmpty()) {
                mediaRepository.delete(noteId, mediaToDelete)
            }

            notesRepository.updateNoteText(noteId, content)

            notesRepository.updateNoteFont(noteId, fontColor, fontFamily, fontSize)

            if (tags.isNotEmpty()) {
                tagsRepository.insert(noteId, tags)
            }
            if (tagsToDelete.isNotEmpty()) {
                tagsRepository.deleteForNote(noteId, tagsToDelete)
            }

            tagsRepository.deleteUnusedTags()
        }
    }
}