package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.storage.internal.database.notes.entities.EntryNoteLocation

@Dao
internal interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: EntryNoteLocation)

    @Query("DELETE FROM ${EntryNoteLocation.TABLE_NAME} WHERE ${EntryNoteLocation.FIELD_NOTE_ID} = :noteId")
    suspend fun delete(noteId: String)
}