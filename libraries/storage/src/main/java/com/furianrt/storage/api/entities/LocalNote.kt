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

    sealed class Content(val id: String, val position: Int) {

        class Title(id: String, position: Int, val text: String) : Content(id, position)

        class MediaBlock(
            id: String,
            position: Int,
            val media: List<Media>,
        ) : Content(id, position)

        sealed class Media(
            val id: String,
            val uri: Uri,
            val ratio: Float,
            val position: Int,
        )

        class Image(
            id: String,
            uri: Uri,
            ratio: Float,
            position: Int,
        ) : Media(id, uri, ratio, position)

        class Video(
            id: String,
            uri: Uri,
            ratio: Float,
            position: Int,
            val duration: Int,
        ) : Media(id, uri, ratio, position)
    }
}
