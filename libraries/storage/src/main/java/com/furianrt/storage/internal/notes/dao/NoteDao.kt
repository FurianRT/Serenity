package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Query
import com.furianrt.storage.internal.notes.entities.EntryNote

@Dao
internal interface NoteDao {

    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    suspend fun getAllNotes(): List<EntryNote>
}
