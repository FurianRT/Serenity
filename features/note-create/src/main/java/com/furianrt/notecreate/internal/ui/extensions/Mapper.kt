package com.furianrt.notecreate.internal.ui.extensions

import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.storage.api.entities.SimpleNote

internal fun NoteItem.toSimpleNote() = SimpleNote(
    id = id,
    timestamp = timestamp,
)