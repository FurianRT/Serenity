package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.notes.dao.TagDao
import com.furianrt.storage.internal.notes.entities.EntryNoteToTag
import com.furianrt.storage.internal.notes.mappers.toEntryNoteTag
import com.furianrt.storage.internal.notes.mappers.toEntryNoteToTag
import javax.inject.Inject

internal class TagsRepositoryImp @Inject constructor(
    private val tagDao: TagDao,
    private val noteToTagDao: NoteToTagDao,
    private val transactionsHelper: TransactionsHelper,
) : TagsRepository {

    override suspend fun upsert(noteId: String, tag: LocalNote.Tag, inTransaction: Boolean) {
        val action: suspend () -> Unit = {
            tagDao.upsert(tag.toEntryNoteTag())
            noteToTagDao.upsert(tag.toEntryNoteToTag(noteId))
        }
        if (inTransaction) {
            transactionsHelper.startTransaction(action)
        } else {
            action.invoke()
        }
    }

    override suspend fun deleteForNote(
        noteId: String,
        tagId: String,
    ) = noteToTagDao.delete(EntryNoteToTag(noteId, tagId))

    override suspend fun deleteUnusedTags() = tagDao.deleteTagsWithoutNotes()
}
