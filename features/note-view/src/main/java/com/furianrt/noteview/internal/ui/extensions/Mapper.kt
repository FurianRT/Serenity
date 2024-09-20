package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.noteview.internal.ui.entites.NoteItem
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.uikit.extensions.toDateString

internal fun LocalNote.toNoteItem() = NoteItem(
    id = id,
    date = timestamp.toDateString(),
)
