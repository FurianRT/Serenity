package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EntryDeletedNote.TABLE_NAME)
internal class EntryDeletedNote(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey
    val id: String,
) {

    companion object {
        const val TABLE_NAME = "DeletedNotes"
        const val FIELD_ID = "id"
    }
}