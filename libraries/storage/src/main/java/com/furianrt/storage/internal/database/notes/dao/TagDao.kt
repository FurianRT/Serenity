package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag
import com.furianrt.storage.internal.database.notes.entities.TagWithRelatedNoteIds
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: EntryNoteTag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tags: List<EntryNoteTag>)

    @Transaction
    @Query("SELECT * FROM ${EntryNoteTag.TABLE_NAME}")
    fun getAllTags(): Flow<List<TagWithRelatedNoteIds>>

    @Transaction
    @Query(
        "SELECT ${EntryNoteTag.TABLE_NAME}.* FROM ${EntryNoteTag.TABLE_NAME} " +
                "INNER JOIN ${EntryNoteToTag.TABLE_NAME} ON " +
                "${EntryNoteTag.TABLE_NAME}.${EntryNoteTag.FIELD_TITLE} = ${EntryNoteToTag.FIELD_TAG_TITLE} " +
                "AND ${EntryNoteToTag.FIELD_NOTE_ID} = :noteId"
    )
    fun getTags(noteId: String): Flow<List<EntryNoteTag>>

    @Transaction
    @Query(
        "DELETE FROM ${EntryNoteTag.TABLE_NAME} WHERE NOT EXISTS (" +
                "SELECT * FROM ${EntryNoteToTag.TABLE_NAME} WHERE " +
                "${EntryNoteTag.TABLE_NAME}.${EntryNoteTag.FIELD_TITLE} = " +
                "${EntryNoteToTag.TABLE_NAME}.${EntryNoteToTag.FIELD_TAG_TITLE}" +
                ")",
    )
    suspend fun deleteTagsWithoutNotes()
}
