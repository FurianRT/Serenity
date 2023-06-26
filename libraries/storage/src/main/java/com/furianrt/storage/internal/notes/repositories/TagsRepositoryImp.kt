package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.notes.dao.TagDao
import com.furianrt.storage.internal.notes.mappers.toEntryNoteTag
import javax.inject.Inject

internal class TagsRepositoryImp @Inject constructor(
    private val tagDao: TagDao,
) : TagsRepository {
    override suspend fun upsert(tag: LocalNote.Tag) = tagDao.upsert(tag.toEntryNoteTag())

    override suspend fun deleteTagsWithoutNotes() = tagDao.deleteTagsWithoutNotes()
}
