package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote
import kotlinx.coroutines.flow.Flow

interface TagsRepository {
    suspend fun insert(noteId: String, tag: LocalNote.Tag)
    suspend fun insert(noteId: String, tags: List<LocalNote.Tag>)
    suspend fun getTags(noteId: String): Flow<List<LocalNote.Tag>>
    suspend fun deleteForNote(noteId: String, tagIds: List<String>)
    suspend fun deleteUnusedTags()
}
