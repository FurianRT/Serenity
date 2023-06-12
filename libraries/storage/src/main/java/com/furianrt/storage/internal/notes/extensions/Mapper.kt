package com.furianrt.storage.internal.notes.extensions

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.notes.entities.EntryNote

internal fun EntryNote.toNote() = LocalNote(
    id = id,
    time = timestamp,
    title = "",
)
