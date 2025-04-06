package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.storage.internal.database.notes.entities.EntryDeletedNote

@Dao
internal interface DeletedNoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: EntryDeletedNote)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notes: List<EntryDeletedNote>)

    @Query("DELETE FROM ${EntryDeletedNote.TABLE_NAME}")
    suspend fun clear()
}