package com.furianrt.storage.internal.notes.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal class LinkedNote(
    @Embedded
    val note: EntryNote,

    @Relation(
        entity = EntryNoteTitle::class,
        parentColumn = EntryNote.FIELD_ID,
        entityColumn = EntryNoteTitle.FIELD_NOTE_ID,
    )
    val titles: List<EntryNoteTitle>,

    @Relation(
        entity = EntryNoteImage::class,
        parentColumn = EntryNote.FIELD_ID,
        entityColumn = EntryNoteImage.FIELD_NOTE_ID,
    )
    val images: List<EntryNoteImage>,

    @Relation(
        entity = EntryNoteTag::class,
        parentColumn = EntryNoteTag.FIELD_ID,
        entityColumn = EntryNote.FIELD_ID,
        associateBy = Junction(
            value = EntryNoteToTag::class,
            parentColumn = EntryNoteToTag.FIELD_TAG_ID,
            entityColumn = EntryNoteToTag.FIELD_NOTE_ID,
        ),
    )
    val tags: List<EntryNoteTag>,
)
