package com.furianrt.notecreate.internal.ui.extensions

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notelistui.entities.UiNoteTheme

internal fun SimpleNote.toNoteItem(
    theme: UiNoteTheme?,
) = NoteItem(
    id = id,
    theme = theme,
    date = date,
    isPinned = isPinned,
)
