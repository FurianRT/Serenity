package com.furianrt.notecreate.internal.ui.extensions

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.notecreate.internal.ui.entites.NoteItem

internal fun NoteItem.toSimpleNote() = SimpleNote(
    id = id,
    date = date,
)