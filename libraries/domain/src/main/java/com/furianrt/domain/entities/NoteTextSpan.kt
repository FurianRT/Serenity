package com.furianrt.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NoteTextSpan(
    open val titleId: String,
    open val start: Int,
    open val end: Int,
) {
    @Serializable
    @SerialName("Bold")
    data class Bold(
        @SerialName("bold_id")
        override val titleId: String,
        @SerialName("bold_start")
        override val start: Int,
        @SerialName("bold_end")
        override val end: Int,
    ) : NoteTextSpan(titleId, start, end)

    @Serializable
    @SerialName("Italic")
    data class Italic(
        @SerialName("italic_id")
        override val titleId: String,
        @SerialName("italic_start")
        override val start: Int,
        @SerialName("italic_end")
        override val end: Int,
    ) : NoteTextSpan(titleId, start, end)

    @Serializable
    @SerialName("Underline")
    data class Underline(
        @SerialName("underline_id")
        override val titleId: String,
        @SerialName("underline_start")
        override val start: Int,
        @SerialName("underline_end")
        override val end: Int,
    ) : NoteTextSpan(titleId, start, end)

    @Serializable
    @SerialName("Strikethrough")
    data class Strikethrough(
        @SerialName("strikethrough_id")
        override val titleId: String,
        @SerialName("strikethrough_start")
        override val start: Int,
        @SerialName("strikethrough_end")
        override val end: Int,
    ) : NoteTextSpan(titleId, start, end)

    @Serializable
    @SerialName("FontColor")
    data class FontColor(
        @SerialName("font_color_id")
        override val titleId: String,
        @SerialName("font_color_start")
        override val start: Int,
        @SerialName("font_color_end")
        override val end: Int,
        val color: Int,
    ) : NoteTextSpan(titleId, start, end)

    @Serializable
    @SerialName("FillColor")
    data class FillColor(
        @SerialName("fill_color_id")
        override val titleId: String,
        @SerialName("fill_color_start")
        override val start: Int,
        @SerialName("fill_color_end")
        override val end: Int,
        val color: Int,
    ) : NoteTextSpan(titleId, start, end)
}