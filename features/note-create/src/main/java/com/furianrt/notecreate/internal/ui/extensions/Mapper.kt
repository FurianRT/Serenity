package com.furianrt.notecreate.internal.ui.extensions

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.notecreate.internal.ui.entites.NoteItem

internal fun SimpleNote.toNoteItem() = NoteItem(
    id = id,
    date = date,
    isPinned = isPinned,
)
