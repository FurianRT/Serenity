package com.furianrt.serenity.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.uikit.extensions.toDateString

internal fun List<LocalNote>.toMainScreenNotes() = mapImmutable(LocalNote::toMainScreenNote)

internal fun LocalNote.toMainScreenNote() = MainScreenNote(
    id = id,
    date = timestamp.toDateString(),
    tags = tags.mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
)
