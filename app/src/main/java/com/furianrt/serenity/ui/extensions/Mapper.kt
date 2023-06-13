package com.furianrt.serenity.ui.extensions

import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.notecontent.extensions.toUiNoteTag
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.storage.api.entities.LocalNote
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

internal fun List<LocalNote>.toMainScreenNotes() = map(LocalNote::toMainScreenNote)
    .toImmutableList()

internal fun LocalNote.toMainScreenNote() = MainScreenNote(
    id = id,
    timestamp = timestamp,
    tags = tags.map(LocalNote.Tag::toUiNoteTag).toImmutableSet(),
    content = content.map(LocalNote.Content::toUiNoteContent).toImmutableSet(),
)
