package com.furianrt.notelistui.entities

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
sealed class UiNoteBackground(
    open val id: String,
    open val isLight: Boolean,
    open val colorScheme: ColorScheme,
) {

    @Immutable
    data class Solid(
        override val id: String,
        override val isLight: Boolean,
        override val colorScheme: ColorScheme,
    ) : UiNoteBackground(id, isLight, colorScheme)

    @Immutable
    data class Gradient(
        override val id: String,
        override val isLight: Boolean,
        override val colorScheme: ColorScheme,
        val colorStart: Color,
        val colorEnd: Color,
    ) : UiNoteBackground(id, isLight, colorScheme)
}