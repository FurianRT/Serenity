package com.furianrt.storage.internal.database.notes.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
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
