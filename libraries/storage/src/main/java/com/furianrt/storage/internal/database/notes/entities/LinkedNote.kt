package com.furianrt.storage.internal.database.notes.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal class LinkedNote(
    @Embedded
    val note: EntryNote,

    @Relation(
        entity = EntryNoteImage::class,
        entityColumn = EntryNoteImage.FIELD_NOTE_ID,
        parentColumn = EntryNote.FIELD_ID,
    )
    val images: List<EntryNoteImage>,

    @Relation(
        entity = EntryNoteVideo::class,
        entityColumn = EntryNoteImage.FIELD_NOTE_ID,
        parentColumn = EntryNote.FIELD_ID,
    )
    val videos: List<EntryNoteVideo>,

    @Relation(
        entity = EntryNoteVoice::class,
        entityColumn = EntryNoteVoice.FIELD_NOTE_ID,
        parentColumn = EntryNote.FIELD_ID,
    )
    val voices: List<EntryNoteVoice>,

    @Relation(
        entity = EntryNoteTag::class,
        entityColumn = EntryNoteTag.FIELD_TITLE,
        parentColumn = EntryNote.FIELD_ID,
        associateBy = Junction(
            value = EntryNoteToTag::class,
            entityColumn = EntryNoteToTag.FIELD_TAG_TITLE,
            parentColumn = EntryNoteToTag.FIELD_NOTE_ID,
        ),
    )
    val tags: List<EntryNoteTag>,
)
