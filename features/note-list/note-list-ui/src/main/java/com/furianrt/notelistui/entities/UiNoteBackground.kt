package com.furianrt.notelistui.entities

import android.graphics.Color
import androidx.compose.runtime.Immutable

@Immutable
sealed class UiNoteBackground(
    open val id: String,
    open val isLight: Boolean,
) {

    @Immutable
    data class Solid(
        override val id: String,
        override val isLight: Boolean,
        val color: Color,
    ) : UiNoteBackground(id, isLight)

    @Immutable
    data class Gradient(
        override val id: String,
        override val isLight: Boolean,
        val colorStart: Color,
        val colorEnd: Color,
    ) : UiNoteBackground(id, isLight)
}