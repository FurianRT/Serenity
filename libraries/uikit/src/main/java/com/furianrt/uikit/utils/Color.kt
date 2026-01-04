package com.furianrt.uikit.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.brighterBy(percent: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = (hsl[2] + percent).coerceIn(0f, 1f)
    return Color(ColorUtils.HSLToColor(hsl))
}