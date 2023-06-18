package com.furianrt.storage.internal.notes.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.LinkedNote
import kotlinx.coroutines.flow.Flow

@Dao
internal interface NoteDao {
    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    fun getAllLinkedNotes(): Flow<List<LinkedNote>>

    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    fun getAllNotes(): Flow<List<EntryNote>>

    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME} WHERE ${EntryNote.FIELD_ID} = :noteId")
    suspend fun getNote(noteId: String): LinkedNote?
}
