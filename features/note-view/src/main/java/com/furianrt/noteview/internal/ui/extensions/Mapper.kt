package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.noteview.internal.ui.entites.NoteItem
import com.furianrt.domain.entities.LocalNote
import com.furianrt.notelistui.entities.UiNoteTheme

internal fun LocalNote.toNoteItem(theme: UiNoteTheme?) = NoteItem(
    id = id,
    theme = theme,
    date = date,
    isPinned = isPinned,
)
