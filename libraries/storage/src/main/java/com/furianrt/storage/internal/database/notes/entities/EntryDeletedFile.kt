package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EntryDeletedFile.TABLE_NAME)
internal class EntryDeletedFile(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = FIELD_NOTE_ID)
    val noteId: String,
) {
    companion object {
        const val TABLE_NAME = "DeletedFiles"
        const val FIELD_ID = "id"
        const val FIELD_NOTE_ID = "note_id"
    }
}