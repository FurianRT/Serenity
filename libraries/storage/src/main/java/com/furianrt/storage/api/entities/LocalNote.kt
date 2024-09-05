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

    sealed interface Content {

        class Title(val text: String) : Content

        class MediaBlock(val media: List<Media>) : Content

        sealed class Media(
            val id: String,
            val uri: Uri,
            val ratio: Float,
            val date: Long,
        )

        class Image(
            id: String,
            uri: Uri,
            ratio: Float,
            date: Long,
        ) : Media(id, uri, ratio, date)

        class Video(
            id: String,
            uri: Uri,
            ratio: Float,
            date: Long,
            val duration: Int,
        ) : Media(id, uri, ratio, date)
    }
}
