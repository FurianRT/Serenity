package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Query
import com.furianrt.storage.internal.notes.entities.DbNote

@Dao
internal interface NoteDao {

    @Query("SELECT * FROM ${DbNote.TABLE_NAME}")
    suspend fun getAllNotes(): List<DbNote>
}
