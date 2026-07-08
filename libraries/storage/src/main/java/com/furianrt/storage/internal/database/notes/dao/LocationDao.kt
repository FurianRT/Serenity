package com.furianrt.storage.internal.database.notes.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.furianrt.storage.internal.database.notes.entities.EntryNoteLocation

@Dao
internal interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: EntryNoteLocation)

    @Query("DELETE FROM ${EntryNoteLocation.TABLE_NAME} WHERE ${EntryNoteLocation.FIELD_NOTE_ID} = :noteId")
    suspend fun delete(noteId: String)
}