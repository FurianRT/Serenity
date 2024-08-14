package com.furianrt.storage.internal.database.notes.entities

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
        entity = EntryContentBlock::class,
        entityColumn = EntryContentBlock.FIELD_NOTE_ID,
        parentColumn = EntryNote.FIELD_ID,
    )
    val contentBlocks: List<LinkedContentBlock>,

    @Relation(
        entity = EntryNoteTag::class,
        entityColumn = EntryNoteTag.FIELD_ID,
        parentColumn = EntryNote.FIELD_ID,
        associateBy = Junction(
            value = EntryNoteToTag::class,
            entityColumn = EntryNoteToTag.FIELD_TAG_ID,
            parentColumn = EntryNoteToTag.FIELD_NOTE_ID,
        ),
    )
    val tags: List<EntryNoteTag>,
)
