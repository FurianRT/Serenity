package com.furianrt.storage.internal.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = EntryContentBlock.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = EntryNote::class,
            parentColumns = [EntryNote.FIELD_ID],
            childColumns = [EntryContentBlock.FIELD_NOTE_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal class EntryContentBlock(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_NOTE_ID, index = true)
    val noteId: String,

    @ColumnInfo(name = FIELD_POSITION)
    val position: Int,
) {
    companion object {
        const val TABLE_NAME = "ContentBlocks"
        const val FIELD_ID = "id"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_POSITION = "position"
    }
}
