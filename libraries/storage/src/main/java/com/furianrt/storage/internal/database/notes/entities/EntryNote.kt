package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.storage.internal.database.notes.entities.EntryNote.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
internal class EntryNote(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = FIELD_TEXT)
    val text: String,

    @ColumnInfo(name = FIELD_TIMESTAMP)
    val timestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "Notes"
        const val FIELD_ID = "id"
        const val FIELD_TEXT = "text"
        const val FIELD_TIMESTAMP = "timestamp"
    }
}

@Entity
internal class PartNoteText(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_TEXT)
    val text: String,
)
