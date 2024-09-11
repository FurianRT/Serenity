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
            val id: String,
            val uri: Uri,
            val ratio: Float,
            val addedTime: Long,
        )

        class Image(
            id: String,
            uri: Uri,
            ratio: Float,
            addedTime: Long,
        ) : Media(id, uri, ratio, addedTime)

        class Video(
            id: String,
            uri: Uri,
            ratio: Float,
            addedTime: Long,
            val duration: Int,
        ) : Media(id, uri, ratio, addedTime)
    }
}
