package com.furianrt.mediaselector.internal.ui.entities

import android.net.Uri

internal sealed class MediaItem(
    open val id: Long,
    open val uri: Uri,
    open val ratio: Float,
    open val state: SelectionState,
) {
    val isSelected: Boolean
        get() = state is SelectionState.Selected

    fun changeState(state: SelectionState): MediaItem = when (this) {
        is Image -> copy(state = state)
        is Video -> copy(state = state)
    }

    data class Image(
        override val id: Long,
        override val uri: Uri,
        override val ratio: Float,
        override val state: SelectionState,
    ) : MediaItem(id, uri, ratio, state)

    data class Video(
        override val id: Long,
        override val uri: Uri,
        override val ratio: Float,
        override val state: SelectionState,
        val duration: String,
    ) : MediaItem(id, uri, ratio, state)
}
