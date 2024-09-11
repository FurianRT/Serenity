package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag

@Dao
internal interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: EntryNoteTag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tags: List<EntryNoteTag>)

    @Query(
        "DELETE FROM ${EntryNoteTag.TABLE_NAME} WHERE NOT EXISTS (" +
            "SELECT * FROM ${EntryNoteToTag.TABLE_NAME} WHERE " +
            "${EntryNoteTag.TABLE_NAME}.${EntryNoteTag.FIELD_ID} = " +
            "${EntryNoteToTag.TABLE_NAME}.${EntryNoteToTag.FIELD_TAG_ID}" +
            ")",
    )
    suspend fun deleteTagsWithoutNotes()
}
