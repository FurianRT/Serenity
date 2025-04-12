package com.furianrt.backup.internal.domain.entities

import java.time.Instant

internal sealed class RemoteFile(
    open val id: String,
    open val name: String,
) {
    class Image(
        override val id: String,
        override val name: String,
    ) : RemoteFile(id, name)

    class Video(
        override val id: String,
        override val name: String,
    ) : RemoteFile(id, name)

    class Voice(
        override val id: String,
        override val name: String,
    ) : RemoteFile(id, name)

    class NotesData(
        override val id: String,
        override val name: String,
        val createdAt: Instant,
    ) : RemoteFile(id, name)
}