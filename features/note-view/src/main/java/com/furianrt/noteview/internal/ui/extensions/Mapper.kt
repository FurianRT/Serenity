package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.notecontent.extensions.toUiNoteTag
import com.furianrt.noteview.internal.ui.container.entites.ContainerScreenNote
import com.furianrt.storage.api.entities.LocalNote
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

internal fun List<LocalNote>.toContainerScreenNotes() = map(LocalNote::toContainerScreenNote)
    .toImmutableList()

internal fun LocalNote.toContainerScreenNote() = ContainerScreenNote(
    id = id,
    timestamp = timestamp,
    tags = tags.map(LocalNote.Tag::toUiNoteTag).toImmutableSet(),
    content = content.map(LocalNote.Content::toUiNoteContent).toImmutableSet(),
)
