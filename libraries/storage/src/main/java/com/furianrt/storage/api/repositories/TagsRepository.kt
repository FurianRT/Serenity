package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote

interface TagsRepository {
    suspend fun upsert(tag: LocalNote.Tag)

    suspend fun deleteTagsWithoutNotes()
}
