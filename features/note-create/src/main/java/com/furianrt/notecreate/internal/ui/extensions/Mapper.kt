package com.furianrt.notecreate.internal.ui.extensions

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notelistui.entities.UiNoteBackground

internal fun SimpleNote.toNoteItem(
    background: UiNoteBackground?,
) = NoteItem(
    id = id,
    background = background,
    date = date,
    isPinned = isPinned,
)
