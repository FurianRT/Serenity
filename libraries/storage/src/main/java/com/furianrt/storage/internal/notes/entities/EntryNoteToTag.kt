package com.furianrt.storage.internal.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = EntryNoteToTag.TABLE_NAME,
    primaryKeys = [EntryNoteToTag.FIELD_TAG_ID, EntryNoteToTag.FIELD_NOTE_ID],
    foreignKeys = [
        ForeignKey(
            entity = EntryNoteTag::class,
            parentColumns = [EntryNoteTag.FIELD_ID],
            childColumns = [EntryNoteToTag.FIELD_TAG_ID],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = EntryNote::class,
            parentColumns = [EntryNote.FIELD_ID],
            childColumns = [EntryNoteToTag.FIELD_NOTE_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal class EntryNoteToTag(
    @ColumnInfo(name = FIELD_NOTE_ID, index = true)
    val noteId: String,

    @ColumnInfo(name = FIELD_TAG_ID, index = true)
    val tagId: String,
) {
    companion object {
        const val TABLE_NAME = "NoteToTag"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_TAG_ID = "tag_id"
    }
}
