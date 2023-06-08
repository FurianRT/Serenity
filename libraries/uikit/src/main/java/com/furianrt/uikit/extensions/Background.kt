package com.furianrt.uikit.extensions

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.addSerenityBackground() = background(Color.Black)
    .background(
        Brush.verticalGradient(
            listOf(Color.Transparent, Color(0x80001930)),
        ),
    )
    .background(
        Brush.radialGradient(
            colors = listOf(Color(0xFF0B2B48), Color.Transparent),
            radius = 1350f,
            center = Offset.Zero,
        ),
    )
