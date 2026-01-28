package com.furianrt.notelistui.entities

import androidx.annotation.DrawableRes

data class UiNoteBackgroundImage(
    val id: String,
    @get:DrawableRes val resId: Int,
    val scaleType: ScaleType = ScaleType.REPEAT,
) {
    enum class ScaleType {
        REPEAT,
        FILL,
        CENTER,
    }
}