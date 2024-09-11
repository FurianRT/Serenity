package com.furianrt.storage.api.entities

import android.net.Uri

class LocalNote(
    val id: String,
    val timestamp: Long,
    val tags: List<Tag>,
    val content: List<Content>,
) {
    class Tag(
        val id: String,
        val title: String,
    )

    sealed class Content(val id: String) {

        class Title(id: String, val text: String) : Content(id)

        class MediaBlock(id: String, val media: List<Media>) : Content(id)

        sealed class Media(
            open val id: String,
            open val uri: Uri,
            open val ratio: Float,
            open val addedTime: Long,
        )

        data class Image(
            override val id: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedTime: Long,
        ) : Media(id, uri, ratio, addedTime)

        data class Video(
            override val id: String,
            override val uri: Uri,
            override val ratio: Float,
            override val addedTime: Long,
            val duration: Int,
        ) : Media(id, uri, ratio, addedTime)
    }
}
