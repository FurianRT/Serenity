package com.furianrt.notecreate.internal.ui.extensions

import com.furianrt.domain.entities.SimpleNote
import com.furianrt.notecreate.internal.ui.entites.NoteItem
import com.furianrt.notelistui.extensions.toNoteFontColor
import com.furianrt.notelistui.extensions.toNoteFontFamily
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily

internal fun NoteItem.toSimpleNote() = SimpleNote(
    id = id,
    date = date,
    font = fontFamily.toNoteFontFamily(),
    fontColor = fontColor.toNoteFontColor(),
    fontSize = fontSize,
    isPinned = isPinned,
)

internal fun SimpleNote.toNoteItem() = NoteItem(
    id = id,
    date = date,
    fontFamily = font.toUiNoteFontFamily(),
    fontColor = fontColor.toUiNoteFontColor(),
    fontSize = fontSize,
    isPinned = isPinned,
)
