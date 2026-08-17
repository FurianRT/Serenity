package com.furianrt.mediaselector.internal.ui.entities

import android.net.Uri
import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
internal sealed class MediaItem(
    open val id: Long,
    open val name: String,
    open val uri: Uri,
    open val ratio: Float,
    open val state: SelectionState,
    open val album: Album?,
    open val date: LocalDate,
    open val isCameraItem: Boolean,
) {
    val isSelected: Boolean
        get() = state is SelectionState.Counter || state is SelectionState.Single

    fun changeState(state: SelectionState): MediaItem = when (this) {
        is Image -> copy(state = state)
        is Video -> copy(state = state)
    }

    data class Album(
        val id: String,
        val name: String,
    )

    @Immutable
    data class Image(
        override val id: Long,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val state: SelectionState,
        override val album: Album?,
        override val date: LocalDate,
        override val isCameraItem: Boolean = false,
    ) : MediaItem(id, name, uri, ratio, state, album, date, isCameraItem)

    @Immutable
    data class Video(
        override val id: Long,
        override val name: String,
        override val uri: Uri,
        override val ratio: Float,
        override val state: SelectionState,
        override val album: Album?,
        override val date: LocalDate,
        override val isCameraItem: Boolean = false,
        val duration: Int,
    ) : MediaItem(id, name, uri, ratio, state, album, date, isCameraItem)
}
