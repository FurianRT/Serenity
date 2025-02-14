package com.furianrt.notepage.internal.ui.stickers

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
internal class StickerState(
    initialScale: Float = 1f,
    initialRotation: Float = 0f,
    initialIsFlipped: Boolean = false,
    initialAnchors: ImmutableList<Anchor> = persistentListOf(),
    initialEditTime: Long = System.currentTimeMillis(),
    initialIsEditing: Boolean = true,
) {
    var isFlipped: Boolean by mutableStateOf(initialIsFlipped)

    var scale: Float by mutableFloatStateOf(initialScale)

    var rotation: Float by mutableFloatStateOf(initialRotation)

    var anchors: ImmutableList<Anchor> by mutableStateOf(initialAnchors)

    var editTime: Long by mutableLongStateOf(initialEditTime)

    var isEditing: Boolean by mutableStateOf(initialIsEditing)

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