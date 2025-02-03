package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
internal class StickerState(
    initialScale: Float = 1f,
    initialRotation: Float = 0f,
    initialAnchors: ImmutableList<Anchor> = persistentListOf(),
) {

    var scale: Float by mutableFloatStateOf(initialScale)

    var rotation: Float by mutableFloatStateOf(initialRotation)

    var anchors: ImmutableList<Anchor> by mutableStateOf(initialAnchors)

    sealed interface Anchor {
        data class Item(
            val id: String,
            val biasX: Float = 0.5f,
            val biasY: Float = 0.5f,
        ) : Anchor

        data class ViewPort(
            val biasX: Float = 0.5f,
            val biasY: Float = 0.4f,
        ) : Anchor
    }
}