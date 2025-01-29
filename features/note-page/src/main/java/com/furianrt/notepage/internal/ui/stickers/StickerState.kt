package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
internal class StickerState(
    initialScale: Float = 1f,
    initialRotation: Float = 0f,
    initialAnchorId: String? = null,
    initialBiasX: Float = 0.5f,
    initialBiasY: Float = 0.5f,
) {

    var scale: Float by mutableFloatStateOf(initialScale)

    var rotation: Float by mutableFloatStateOf(initialRotation)

    var anchorId: String? by mutableStateOf(initialAnchorId)

    var biasX: Float by mutableFloatStateOf(initialBiasX)

    var biasY: Float by mutableFloatStateOf(initialBiasY)
}