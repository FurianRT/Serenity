package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
internal class StickerState(
    initialScale: Float = 1f,
    initialRotation: Float = 0f,
    initialIsFlipped: Boolean = false,
    initialBiasX: Float = 0.5f,
    initialDpOffsetY: Dp = 0.dp,
    initialEditTime: Long = System.currentTimeMillis(),
    initialIsEditing: Boolean = true,
) {
    var isFlipped: Boolean by mutableStateOf(initialIsFlipped)

    var scale: Float by mutableFloatStateOf(initialScale)

    var rotation: Float by mutableFloatStateOf(initialRotation)

    var biasX: Float by mutableFloatStateOf(initialBiasX)

    var dpOffsetY: Dp by mutableStateOf(initialDpOffsetY)

    var editTime: Long by mutableLongStateOf(initialEditTime)

    var isEditing: Boolean by mutableStateOf(initialIsEditing)
}