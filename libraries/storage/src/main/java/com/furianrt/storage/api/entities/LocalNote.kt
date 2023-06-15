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

    sealed class Content(val position: Int) {
        class TitlesBlock(position: Int, val titles: List<Title>) : Content(position)
        class Title(val id: String, val text: String)

        class ImagesBlock(position: Int, val titles: List<Image>) : Content(position)
        class Image(val id: String, val uri: String)
    }
}
