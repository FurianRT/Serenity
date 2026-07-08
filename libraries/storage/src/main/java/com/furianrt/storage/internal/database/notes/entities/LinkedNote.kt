package com.furianrt.storage.internal.database.notes.entities

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation

internal class LinkedNote(
    @Embedded
    val note: EntryNote,

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

    @Relation(
        entity = EntryNoteVoice::class,
        entityColumns = [EntryNoteVoice.FIELD_NOTE_ID],
        parentColumns = [EntryNote.FIELD_ID],
    )
    val voices: List<EntryNoteVoice>,

    @Relation(
        entity = EntryNoteTag::class,
        entityColumns = [EntryNoteTag.FIELD_TITLE],
        parentColumns = [EntryNote.FIELD_ID],
        associateBy = Junction(
            value = EntryNoteToTag::class,
            entityColumns = [EntryNoteToTag.FIELD_TAG_TITLE],
            parentColumns = [EntryNoteToTag.FIELD_NOTE_ID],
        ),
    )
    val tags: List<EntryNoteTag>,

    @Relation(
        entity = EntryNoteSticker::class,
        entityColumns = [EntryNoteSticker.FIELD_NOTE_ID],
        parentColumns = [EntryNote.FIELD_ID],
    )
    val stickers: List<EntryNoteSticker>,

    @Relation(
        entity = EntryNoteLocation::class,
        entityColumns = [EntryNoteLocation.FIELD_NOTE_ID],
        parentColumns = [EntryNote.FIELD_ID],
    )
    val location: EntryNoteLocation?,
)
