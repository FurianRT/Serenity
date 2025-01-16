package com.furianrt.notelist.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.notelistui.extensions.getShortUiContent
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.uikit.extensions.toDateString

internal fun List<LocalNote>.toMainScreenNotes() = mapImmutable(LocalNote::toMainScreenNote)

internal fun LocalNote.toMainScreenNote() = NoteListScreenNote(
    id = id,
    date = date.toDateString(),
    tags = tags.take(3).mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    fontColor = fontColor.toUiNoteFontColor(),
    content = content.getShortUiContent(),
)
