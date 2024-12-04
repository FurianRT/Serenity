package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.domain.TransactionsHelper
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.domain.repositories.TagsRepository
import com.furianrt.storage.internal.database.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag
import com.furianrt.storage.internal.database.notes.entities.TagWithRelatedNoteIds
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteTag
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteToTag
import com.furianrt.storage.internal.database.notes.mappers.toLocalTag
import com.furianrt.storage.internal.database.notes.mappers.toNoteContentTag
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class TagsRepositoryImp @Inject constructor(
    private val tagDao: TagDao,
    private val noteToTagDao: NoteToTagDao,
    private val transactionsHelper: TransactionsHelper,
) : TagsRepository {

    override suspend fun insert(noteId: String, tag: LocalNote.Tag) = withContext(NonCancellable) {
        transactionsHelper.startTransaction {
            tagDao.insert(tag.toEntryNoteTag())
            noteToTagDao.insert(tag.toEntryNoteToTag(noteId))
        }
    }

    override suspend fun insert(
        noteId: String,
        tags: List<LocalNote.Tag>,
    ) = withContext(NonCancellable) {
        transactionsHelper.startTransaction {
            tagDao.insert(tags.map(LocalNote.Tag::toEntryNoteTag))
            noteToTagDao.insert(tags.map { it.toEntryNoteToTag(noteId) })
        }
    }

    override fun getAllTags(): Flow<List<LocalTag>> {
        return tagDao.getAllTags().deepMap(TagWithRelatedNoteIds::toLocalTag)
    }

    override fun getTags(noteId: String): Flow<List<LocalNote.Tag>> {
        return tagDao.getTags(noteId).deepMap(EntryNoteTag::toNoteContentTag)
    }

    override suspend fun deleteForNote(
        noteId: String,
        tags: List<LocalNote.Tag>,
    ) = noteToTagDao.delete(tags.map { EntryNoteToTag(noteId, it.title) })

    override suspend fun deleteUnusedTags() = tagDao.deleteTagsWithoutNotes()
}
