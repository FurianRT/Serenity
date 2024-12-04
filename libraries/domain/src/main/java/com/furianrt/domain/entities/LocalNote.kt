package com.furianrt.domain.entities

import android.net.Uri
import java.time.ZonedDateTime

class LocalNote(
    val id: String,
    val date: ZonedDateTime,
    val tags: List<Tag>,
    val content: List<Content>,
) {
    class Tag(val title: String)

    sealed class Content(val id: String) {

        class Title(id: String, val text: String) : Content(id)

        class MediaBlock(id: String, val media: List<Media>) : Content(id)

        sealed class Media(
            open val name: String,
            open val uri: Uri,
            open val ratio: Float,
            open val addedDate: ZonedDateTime,
        )

        data class Image(
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedDate: ZonedDateTime,
        ) : Media(name, uri, ratio, addedDate)

        data class Video(
            override val name: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedDate: ZonedDateTime,
            val duration: Int,
        ) : Media(name, uri, ratio, addedDate)
    }
}
