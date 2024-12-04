package com.furianrt.storage.internal.database.notes.entities

import androidx.room.Embedded
import androidx.room.Relation

internal class TagWithRelatedNoteIds(
    @Embedded
    val tag: EntryNoteTag,

    @Relation(
        entity = EntryNoteToTag::class,
        entityColumn = EntryNoteToTag.FIELD_TAG_TITLE,
        parentColumn = EntryNoteTag.FIELD_TITLE,
    )
    val notesIds: List<EntryNoteToTag>,
)