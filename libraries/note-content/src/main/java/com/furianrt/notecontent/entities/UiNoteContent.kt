package com.furianrt.notecontent.entities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface UiNoteContent {
    @Immutable
    data class Title(
        val text: String,
        // val spans: List<Span>,
    ) : UiNoteContent /*{
        data class Span(
            val type: Type,
            val startIndex: Int,
            val endIndex: Int,
        ) {
            enum class Type {
                BOLD, ITALIC, STRIKETHROUGH, UNDERLINE
            }
        }
    }*/

    @Immutable
    data class Image(
        val id: String,
        val url: String,
        val thumbnailUrl: String,
    ) : UiNoteContent

    @Immutable
    data class Tag(
        val id: String,
        val title: String,
    ) : UiNoteContent
}
