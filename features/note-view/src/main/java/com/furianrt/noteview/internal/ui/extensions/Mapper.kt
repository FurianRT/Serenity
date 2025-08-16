package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.noteview.internal.ui.entites.NoteItem
import com.furianrt.domain.entities.LocalNote
import com.furianrt.notelistui.entities.UiNoteBackground

internal fun LocalNote.toNoteItem(background: UiNoteBackground?) = NoteItem(
    id = id,
    background = background,
    date = date,
    isPinned = isPinned,
)
