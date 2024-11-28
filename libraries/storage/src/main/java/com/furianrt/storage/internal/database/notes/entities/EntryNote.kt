package com.furianrt.storage.internal.database.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.storage.internal.database.notes.entities.EntryNote.Companion.TABLE_NAME
import java.time.ZonedDateTime

@Entity(tableName = TABLE_NAME)
internal class EntryNote(
    @ColumnInfo(name = FIELD_ID)
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = FIELD_TEXT)
    val text: String,

    @ColumnInfo(name = FIELD_DATE)
    val date: ZonedDateTime,
) {
    companion object {
        const val TABLE_NAME = "Notes"
        const val FIELD_ID = "id"
        const val FIELD_TEXT = "text"
        const val FIELD_DATE = "date"
    }
}

@Entity
internal class PartNoteText(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_TEXT)
    val text: String,
)

@Entity
internal class PartNoteDate(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_DATE)
    val date: ZonedDateTime,
)
