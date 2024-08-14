package com.furianrt.storage.internal.database.notes.entities

import androidx.room.Embedded
import androidx.room.Relation

internal class LinkedContentBlock(
    @Embedded
    val block: EntryContentBlock,

    @Relation(
        entity = EntryNoteImage::class,
        entityColumn = EntryNoteImage.FIELD_BLOCK_ID,
        parentColumn = EntryContentBlock.FIELD_ID,
    )
    val images: List<EntryNoteImage>,
)
