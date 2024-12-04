package com.furianrt.domain.repositories

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import kotlinx.coroutines.flow.Flow

interface TagsRepository {
    suspend fun insert(noteId: String, tag: LocalNote.Tag)
    suspend fun insert(noteId: String, tags: List<LocalNote.Tag>)
    fun getAllTags(): Flow<List<LocalTag>>
    fun getTags(noteId: String): Flow<List<LocalNote.Tag>>
    suspend fun deleteForNote(noteId: String, tags: List<LocalNote.Tag>)
    suspend fun deleteUnusedTags()
}
