package com.furianrt.storage.internal.database.notes.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

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
