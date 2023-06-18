package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.storage.api.entities.LocalNote

internal fun LocalNote.toContainerScreenNote() = NoteViewScreenNote(
    id = id,
    timestamp = timestamp,
    tags = tags.mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
)
