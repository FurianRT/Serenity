package com.furianrt.notecreate.internal.ui.extensions

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notelistui.extensions.toNoteFontColor
import com.furianrt.notelistui.extensions.toNoteFontFamily

internal fun NoteItem.toSimpleNote() = SimpleNote(
    id = id,
    date = date,
    font = fontFamily.toNoteFontFamily(),
    fontColor = fontColor.toNoteFontColor(),
    fontSize = fontSize,
)