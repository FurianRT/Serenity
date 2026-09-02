package com.furianrt.domain.entities

import android.net.Uri
import java.time.ZonedDateTime

sealed class NoteMedia(
    open val id: String,
    open val noteId: String,
    open val name: String,
    open val noteDate: ZonedDateTime,
    open val uri: Uri,
    open val ratio: Float,
    open val addedDate: ZonedDateTime,
) {
    data class Image(
        override val id: String,
        override val noteId: String,
        override val name: String,
        override val noteDate: ZonedDateTime,
        override val uri: Uri,
        override val ratio: Float,
        override val addedDate: ZonedDateTime,
    ) : NoteMedia(
        id = id,
        noteId = noteId,
        name = name,
        noteDate = noteDate,
        uri = uri,
        ratio = ratio,
        addedDate = addedDate,
    )

    data class Video(
        override val id: String,
        override val noteId: String,
        override val name: String,
        override val noteDate: ZonedDateTime,
        override val uri: Uri,
        override val ratio: Float,
        override val addedDate: ZonedDateTime,
        val duration: Int,
    ) : NoteMedia(
        id = id,
        noteId = noteId,
        name = name,
        noteDate = noteDate,
        uri = uri,
        ratio = ratio,
        addedDate = addedDate,
    )
}