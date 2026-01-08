package com.furianrt.mediaselector.internal.ui.entities

import android.net.Uri

internal sealed class MediaItem(
    open val id: Long,
    open val name: String,
    open val uri: Uri,
    open val ratio: Float,
    open val state: SelectionState,
    open val album: Album,
) {
    val isSelected: Boolean
        get() = state is SelectionState.Selected

    fun changeState(state: SelectionState): MediaItem = when (this) {
        is Image -> copy(state = state)
        is Video -> copy(state = state)
    }

    data class Album(
        val id: String,
        val name: String,
    )

    data class Image(
        override val id: Long,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val state: SelectionState,
        override val album: Album,
    ) : MediaItem(id, name, uri, ratio, state, album)

    data class Video(
        override val id: Long,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val state: SelectionState,
        override val album: Album,
        val duration: Int,
    ) : MediaItem(id, name, uri, ratio, state, album)
}
