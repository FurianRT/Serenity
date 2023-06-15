package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.LinkedNote

@Dao
internal interface NoteDao {
    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    suspend fun getAllLinkedNotes(): List<LinkedNote>

    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    suspend fun getAllNotes(): List<EntryNote>
}
