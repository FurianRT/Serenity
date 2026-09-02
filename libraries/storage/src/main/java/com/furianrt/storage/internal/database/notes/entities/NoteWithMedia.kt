package com.furianrt.storage.internal.database.notes.entities

import androidx.room3.ColumnInfo
import androidx.room3.Relation
import java.time.ZonedDateTime

internal class NoteWithMedia(
    @ColumnInfo(name = EntryNote.FIELD_ID)
    val id: String,

    @ColumnInfo(name = EntryNote.FIELD_DATE)
    val date: ZonedDateTime,

    @Relation(
        entity = EntryNoteImage::class,
        entityColumns = [EntryNoteImage.FIELD_NOTE_ID],
        parentColumns = [EntryNote.FIELD_ID],
    )
    val images: List<EntryNoteImage>,

    @Relation(
        entity = EntryNoteVideo::class,
        entityColumns = [EntryNoteImage.FIELD_NOTE_ID],
        parentColumns = [EntryNote.FIELD_ID],
    )
    val videos: List<EntryNoteVideo>,
)