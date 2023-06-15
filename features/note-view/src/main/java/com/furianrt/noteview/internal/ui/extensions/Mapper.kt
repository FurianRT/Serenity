package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.storage.api.entities.LocalNote
import kotlinx.collections.immutable.toImmutableList

internal fun LocalNote.toContainerScreenNote() = NoteViewScreenNote(
    id = id,
    timestamp = timestamp,
    tags = tags.map(LocalNote.Tag::toRegularUiNoteTag).toImmutableList(),
    content = content.map(LocalNote.Content::toUiNoteContent).toImmutableList(),
)
