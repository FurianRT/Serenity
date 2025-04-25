package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag
import com.furianrt.storage.internal.database.notes.entities.LinkedNote
import com.furianrt.storage.internal.database.notes.entities.PartNoteDate
import com.furianrt.storage.internal.database.notes.entities.PartNoteFont
import com.furianrt.storage.internal.database.notes.entities.PartNoteId
import com.furianrt.storage.internal.database.notes.entities.PartNoteIsPinned
import com.furianrt.storage.internal.database.notes.entities.PartNoteText
import kotlinx.coroutines.flow.Flow

@Dao
internal interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: EntryNote)

    @Upsert
    suspend fun upsert(note: EntryNote)

    @Update(entity = EntryNote::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartNoteText)

    @Update(entity = EntryNote::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartNoteDate)

    @Update(entity = EntryNote::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartNoteFont)

    @Update(entity = EntryNote::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: PartNoteIsPinned)

    @Update(entity = EntryNote::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(data: List<PartNoteIsPinned>)

    @Delete(entity = EntryNote::class)
    suspend fun delete(data: PartNoteId)

    @Delete(entity = EntryNote::class)
    suspend fun delete(data: List<PartNoteId>)

    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME}")
    fun getAllNotes(): Flow<List<LinkedNote>>

    @Transaction
    @Query(
        """
    SELECT * 
    FROM ${EntryNote.TABLE_NAME} 
    WHERE ${EntryNote.FIELD_TEXT} LIKE :query 
    OR EXISTS (
        SELECT 1 
        FROM ${EntryNoteToTag.TABLE_NAME} 
        WHERE ${EntryNoteToTag.TABLE_NAME}.${EntryNoteToTag.FIELD_NOTE_ID} = ${EntryNote.TABLE_NAME}.${EntryNote.FIELD_ID} 
        AND ${EntryNoteToTag.TABLE_NAME}.${EntryNoteToTag.FIELD_TAG_TITLE} LIKE :query
    )
"""
    )
    fun getAllNotes(query: String): Flow<List<LinkedNote>>

    @Transaction
    @Query("SELECT * FROM ${EntryNote.TABLE_NAME} WHERE ${EntryNote.FIELD_ID} = :noteId")
    fun getNote(noteId: String): Flow<LinkedNote?>

}
