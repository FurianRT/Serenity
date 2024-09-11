package com.furianrt.storage.internal.database.notes.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag

@Dao
internal interface TagDao {
    @Upsert
    suspend fun upsert(tag: EntryNoteTag)

    @Upsert
    suspend fun upsert(tags: List<EntryNoteTag>)

    @Query(
        "DELETE FROM ${EntryNoteTag.TABLE_NAME} WHERE NOT EXISTS (" +
            "SELECT * FROM ${EntryNoteToTag.TABLE_NAME} WHERE " +
            "${EntryNoteTag.TABLE_NAME}.${EntryNoteTag.FIELD_ID} = " +
            "${EntryNoteToTag.TABLE_NAME}.${EntryNoteToTag.FIELD_TAG_ID}" +
            ")",
    )
    suspend fun deleteTagsWithoutNotes()
}
