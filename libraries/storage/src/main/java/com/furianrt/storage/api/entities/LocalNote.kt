package com.furianrt.storage.api.entities

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

        class ImagesBlock(
            id: String,
            position: Int,
            val images: List<Image>,
        ) : Content(id, position)

        class Image(val id: String, val uri: String, val position: Int)
    }
}
