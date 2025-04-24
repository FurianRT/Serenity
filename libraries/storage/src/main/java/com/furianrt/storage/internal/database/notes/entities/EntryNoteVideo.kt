package com.furianrt.storage.internal.database.notes.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(
    tableName = EntryNoteVideo.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = EntryNote::class,
            parentColumns = [EntryNote.FIELD_ID],
            childColumns = [EntryNoteVideo.FIELD_NOTE_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal class EntryNoteVideo(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_NAME)
    val name: String,

    @ColumnInfo(name = FIELD_NOTE_ID, index = true)
    val noteId: String,

    @ColumnInfo(name = FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = FIELD_RATIO)
    val ratio: Float,

    @ColumnInfo(name = FIELD_DURATION)
    val duration: Int,

    @ColumnInfo(name = FIELD_ADDED_DATE)
    val addedDate: ZonedDateTime,

    @ColumnInfo(name = FIELD_IS_SAVED)
    val isSaved: Boolean,
) {
    companion object {
        const val TABLE_NAME = "Videos"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_URI = "uri"
        const val FIELD_RATIO = "ratio"
        const val FIELD_DURATION = "duration"
        const val FIELD_ADDED_DATE = "added_date"
        const val FIELD_IS_SAVED = "is_saved"
    }
}

@Entity
internal class PartVideoUri(
    @ColumnInfo(name = EntryNoteVideo.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNoteVideo.FIELD_NAME)
    val name: String,

    @ColumnInfo(name = EntryNoteVideo.FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = EntryNoteVideo.FIELD_IS_SAVED)
    val isSaved: Boolean,
)

@Entity
internal class PartVideoId(
    @ColumnInfo(name = EntryNoteVideo.FIELD_ID)
    val id: String,
)
