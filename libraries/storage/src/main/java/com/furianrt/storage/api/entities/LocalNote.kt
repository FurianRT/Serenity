package com.furianrt.storage.api.entities

class LocalNote(
    val id: String,
    val timestamp: Long,
    val tags: List<Tag>,
    val content: List<Content>,
) {
    sealed interface Content {
        class Title(
            val id: String,
            val text: String,
        ) : Content

        class Image(
            val id: String,
            val uri: String,
        ) : Content
    }

    class Tag(
        val id: String,
        val title: String,
    )
}
