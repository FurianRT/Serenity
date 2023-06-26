package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.furianrt.storage.internal.notes.entities.EntryNoteTitle

@Dao
internal interface NoteTitleDao {
    @Delete
    suspend fun delete(title: EntryNoteTitle)

    @Upsert
    suspend fun upsert(title: EntryNoteTitle)
}
