package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag

@Dao
internal interface NoteToTagDao {
    @Upsert
    suspend fun upsert(noteToTag: EntryNoteToTag)

    @Upsert
    suspend fun upsert(noteToTag: List<EntryNoteToTag>)

    @Delete
    suspend fun delete(tag: EntryNoteToTag)
}
