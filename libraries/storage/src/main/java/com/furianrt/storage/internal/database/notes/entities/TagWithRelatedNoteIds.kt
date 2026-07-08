package com.furianrt.storage.internal.database.notes.entities

import androidx.room3.Embedded
import androidx.room3.Relation

internal class TagWithRelatedNoteIds(
    @Embedded
    val tag: EntryNoteTag,

    @Relation(
        entity = EntryNoteToTag::class,
        entityColumns = [EntryNoteToTag.FIELD_TAG_TITLE],
        parentColumns = [EntryNoteTag.FIELD_TITLE],
    )
    val notesIds: List<EntryNoteToTag>,
)