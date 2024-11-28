package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.LinkedNote
import com.furianrt.storage.internal.database.notes.entities.PartNoteDate
import com.furianrt.storage.internal.database.notes.entities.PartNoteText
import kotlinx.coroutines.flow.Flow

@Dao
internal interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: EntryNote)

    @Update(entity = EntryNote::class)
    suspend fun update(data: PartNoteText)

    @Update(entity = EntryNote::class)
    suspend fun update(data: PartNoteDate)

    @Query("DELETE FROM ${EntryNote.TABLE_NAME} WHERE ${EntryNote.FIELD_ID} = :noteId")
    suspend fun delete(noteId: String)

    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    fun getAllNotes(): Flow<List<LinkedNote>>

    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME} WHERE ${EntryNote.FIELD_ID} = :noteId")
    fun getNote(noteId: String): Flow<LinkedNote?>
}
