package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EntryNoteTag.TABLE_NAME)
internal class EntryNoteTag(
    @ColumnInfo(name = FIELD_TITLE)
    @PrimaryKey
    val title: String,
) {
    companion object {
        const val TABLE_NAME = "Tags"
        const val FIELD_TITLE = "title"
    }
}
