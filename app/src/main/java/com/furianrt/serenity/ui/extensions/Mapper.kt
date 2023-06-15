package com.furianrt.serenity.ui.extensions

import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.storage.api.entities.LocalNote
import kotlinx.collections.immutable.toImmutableList

internal fun List<LocalNote>.toMainScreenNotes() = map(LocalNote::toMainScreenNote)

internal fun LocalNote.toMainScreenNote() = MainScreenNote(
    id = id,
    timestamp = timestamp,
    tags = tags.map(LocalNote.Tag::toRegularUiNoteTag).toImmutableList(),
    content = content.map(LocalNote.Content::toUiNoteContent).toImmutableList(),
)
