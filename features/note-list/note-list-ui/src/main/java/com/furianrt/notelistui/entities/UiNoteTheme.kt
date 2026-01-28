package com.furianrt.notelistui.entities

sealed interface UiNoteTheme {
    data class Solid(
        val color: UiNoteBackground,
    ) : UiNoteTheme

    sealed class Image(
        open val color: UiNoteBackground?,
        open val image: UiNoteBackgroundImage,
    ) : UiNoteTheme {

        data class Picture(
            override val color: UiNoteBackground,
            override val image: UiNoteBackgroundImage,
        ) : Image(color, image)

        data class Pattern(
            override val color: UiNoteBackground?,
            override val image: UiNoteBackgroundImage,
        ) : Image(color, image)
    }

    val colorId: String?
        get() = when (this) {
            is Solid -> color.id
            is Image -> color?.id
        }

    val imageId: String?
        get() = when (this) {
            is Solid -> null
            is Image -> image.id
        }
}