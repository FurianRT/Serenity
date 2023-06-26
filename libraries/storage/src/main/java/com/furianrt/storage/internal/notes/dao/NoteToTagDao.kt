package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.furianrt.storage.internal.notes.entities.EntryNoteToTag

@Dao
internal interface NoteToTagDao {
    @Upsert
    suspend fun upsert(noteToTag: EntryNoteToTag)

    @Delete
    suspend fun delete(tag: EntryNoteToTag)
}
