package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote

interface TagsRepository {
    suspend fun upsert(noteId: String, tag: LocalNote.Tag, inTransaction: Boolean = true)
    suspend fun deleteForNote(noteId: String, tagId: String)
    suspend fun deleteUnusedTags()
}
