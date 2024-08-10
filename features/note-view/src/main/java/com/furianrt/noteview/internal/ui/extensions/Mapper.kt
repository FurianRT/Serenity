package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.noteview.internal.ui.entites.ContainerScreenNote
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import com.furianrt.uikit.extensions.toDateString
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

internal fun LocalNote.toNoteViewScreenNote() = NoteViewScreenNote(
    id = id,
    timestamp = timestamp,
    tags = tags.mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
)

internal fun LocalSimpleNote.toContainerScreenNote() = ContainerScreenNote(
    id = id,
    date = timestamp.toDateString(),
)
