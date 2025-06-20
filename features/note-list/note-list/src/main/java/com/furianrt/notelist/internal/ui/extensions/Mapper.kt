package com.furianrt.notelist.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.notelistui.extensions.getShortUiContent
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.uikit.extensions.toDateString
import java.time.LocalDate

internal fun List<LocalNote>.toMainScreenNotes(
    selectedNotes: Set<String>,
    appFontFamily: NoteFontFamily,
) = mapImmutable { note ->
    note.toMainScreenNote(
        isSelected = selectedNotes.contains(note.id),
        appFontFamily = appFontFamily,
    )
}

internal fun LocalNote.toMainScreenNote(
    isSelected: Boolean,
    appFontFamily: NoteFontFamily,
): NoteListScreenNote {
    val localDateNow = LocalDate.now()
    val localDate = date.toLocalDate()
    return NoteListScreenNote(
        id = id,
        date = when {
            localDateNow == localDate -> NoteListScreenNote.Date.Today
            localDateNow.minusDays(1) == localDate -> NoteListScreenNote.Date.Yesterday
            else -> NoteListScreenNote.Date.Other(date.toDateString())
        },
        tags = tags.take(3).mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
        fontColor = fontColor?.toUiNoteFontColor(),
        fontFamily = fontFamily?.toUiNoteFontFamily(),
        fontSize = fontSize,
        isPinned = isPinned,
        isSelected = isSelected,
        content = content.getShortUiContent((fontFamily ?: appFontFamily).toUiNoteFontFamily()),
    )
}
