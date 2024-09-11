package com.furianrt.storage.internal.database.notes.entities

import android.net.Uri
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
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_NOTE_ID, index = true)
    val noteId: String,

    @ColumnInfo(name = FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = FIELD_RATIO)
    val ratio: Float,

    @ColumnInfo(name = FIELD_ADDED_TIME)
    val addedTime: Long,

    @ColumnInfo(name = FIELD_IS_SAVED)
    val isSaved: Boolean,
) {
    companion object {
        const val TABLE_NAME = "Images"
        const val FIELD_ID = "id"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_URI = "uri"
        const val FIELD_RATIO = "ratio"
        const val FIELD_ADDED_TIME = "added_time"
        const val FIELD_IS_SAVED = "is_saved"
    }
}
