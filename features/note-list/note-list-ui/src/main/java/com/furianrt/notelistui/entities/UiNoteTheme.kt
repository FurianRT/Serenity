package com.furianrt.notelistui.entities

sealed class UiNoteTheme(
    open val isAppTheme: Boolean,
) {
    data class Solid(
        val color: UiNoteBackground,
        override val isAppTheme: Boolean = false,
    ) : UiNoteTheme(isAppTheme)

    sealed class Image(
        open val color: UiNoteBackground?,
        open val image: UiNoteBackgroundImage,
        override val isAppTheme: Boolean,
    ) : UiNoteTheme(isAppTheme) {

        data class Picture(
            override val color: UiNoteBackground,
            override val image: UiNoteBackgroundImage,
            override val isAppTheme: Boolean = false,
        ) : Image(color, image, isAppTheme)

        data class Pattern(
            override val color: UiNoteBackground?,
            override val image: UiNoteBackgroundImage,
            override val isAppTheme: Boolean = false,
        ) : Image(color, image, isAppTheme)
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