package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag

@Dao
internal interface NoteToTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(noteToTag: EntryNoteToTag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(noteToTag: List<EntryNoteToTag>)

    @Delete
    suspend fun delete(tags: List<EntryNoteToTag>)
}
