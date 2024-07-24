package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.notecontent.extensions.toRegularUiNoteTag
import com.furianrt.notecontent.extensions.toUiNoteContent
import com.furianrt.noteview.internal.ui.entites.ContainerScreenNote
import com.furianrt.noteview.internal.ui.entites.NoteViewScreenNote
import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DATE_PATTERN = "dd LLL yyyy"

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

private fun Long.toDateString(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.US)
    return localDateTime.format(formatter)
}
