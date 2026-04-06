package com.furianrt.notelistui.entities

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.furianrt.notelistui.entities.UiNoteBackgroundImage.ScaleType.*

data class UiNoteBackgroundImage(
    val id: String,
    val source: Source,
    val scaleType: ScaleType = REPEAT,
) {
    sealed interface Source {
        class Resource(@get:DrawableRes val resId: Int) : Source
        class Storage(val uri: Uri) : Source
    }

    enum class ScaleType {
        REPEAT,
        FILL,
        CENTER,
        CROP_ALIGN_BOTTOM,
        CROP_ALIGN_CENTER,
        CROP_ALIGN_TOP,
    }
}

fun UiNoteBackgroundImage.ScaleType.toContentScale() = when (this) {
    REPEAT -> ContentScale.FillBounds
    FILL -> ContentScale.FillBounds
    CENTER -> ContentScale.Inside
    CROP_ALIGN_BOTTOM, CROP_ALIGN_CENTER, CROP_ALIGN_TOP -> ContentScale.Crop
}

fun UiNoteBackgroundImage.ScaleType.toContentAlignment() = when (this) {
    REPEAT -> Alignment.Center
    FILL -> Alignment.Center
    CENTER -> Alignment.Center
    CROP_ALIGN_BOTTOM -> Alignment.BottomCenter
    CROP_ALIGN_CENTER -> Alignment.Center
    CROP_ALIGN_TOP -> Alignment.TopCenter
}
