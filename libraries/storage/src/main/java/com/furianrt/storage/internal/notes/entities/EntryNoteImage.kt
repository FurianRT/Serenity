package com.furianrt.storage.internal.notes.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = EntryNoteImage.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = EntryNote::class,
            parentColumns = [EntryNote.FIELD_ID],
            childColumns = [EntryNoteImage.FIELD_NOTE_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal class EntryNoteImage(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_NOTE_ID, index = true)
    val noteId: String,

    @ColumnInfo(name = FIELD_URI)
    val uri: String,

    @ColumnInfo(name = FIELD_POSITION)
    val position: Int,

    @ColumnInfo(name = FIELD_BLOCK_POSITION)
    val blockPosition: Int,
) {
    companion object {
        const val TABLE_NAME = "Images"
        const val FIELD_ID = "id"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_URI = "uri"
        const val FIELD_POSITION = "position"
        const val FIELD_BLOCK_POSITION = "block_position"
    }
}
