package com.furianrt.storage.internal.database.notes.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = EntryNoteVoice.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = EntryNote::class,
            parentColumns = [EntryNote.FIELD_ID],
            childColumns = [EntryNoteVoice.FIELD_NOTE_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal class EntryNoteVoice(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_NOTE_ID, index = true)
    val noteId: String,

    @ColumnInfo(name = FIELD_URI)
    val uri: Uri,

    @ColumnInfo(name = FIELD_DURATION)
    val duration: Int,

    @ColumnInfo(name = FIELD_VOLUME)
    val volume: List<Float>,
) {

    companion object {
        const val TABLE_NAME = "VoiceRecords"
        const val FIELD_ID = "id"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_URI = "uri"
        const val FIELD_DURATION = "duration"
        const val FIELD_VOLUME = "volume"
    }
}

@Entity
internal class PartVoiceId(
    @ColumnInfo(name = EntryNoteVoice.FIELD_ID)
    val id: String,
)
