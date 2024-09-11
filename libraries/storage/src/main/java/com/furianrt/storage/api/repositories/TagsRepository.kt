package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote

interface TagsRepository {
    suspend fun insert(noteId: String, tag: LocalNote.Tag)
    suspend fun insert(noteId: String, tags: List<LocalNote.Tag>)
    suspend fun deleteForNote(noteId: String, tagId: String)
    suspend fun deleteUnusedTags()
}
