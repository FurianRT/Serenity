package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.noteview.internal.ui.entites.NoteItem
import com.furianrt.domain.entities.LocalNote

internal fun LocalNote.toNoteItem() = NoteItem(
    id = id,
    date = date,
    isPinned = isPinned,
)
