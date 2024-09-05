package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.LinkedNote
import kotlinx.coroutines.flow.Flow

@Dao
internal interface NoteDao {
    @Upsert
    suspend fun upsert(note: EntryNote)

    @Query("UPDATE ${EntryNote.TABLE_NAME} SET ${EntryNote.FIELD_TEXT} = :text WHERE ${EntryNote.FIELD_ID} = :noteId")
    suspend fun updateNoteText(noteId: String, text: String)

    @Delete
    suspend fun delete(note: EntryNote)

    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    fun getAllNotes(): Flow<List<LinkedNote>>

    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME} WHERE ${EntryNote.FIELD_ID} = :noteId")
    fun getNote(noteId: String): Flow<LinkedNote?>
}
