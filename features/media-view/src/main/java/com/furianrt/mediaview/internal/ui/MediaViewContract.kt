package com.furianrt.mediaview.internal.ui

import com.furianrt.mediaview.internal.ui.entities.MediaItem
import kotlinx.collections.immutable.ImmutableList

internal sealed interface MediaViewUiState {
    data class Success(
        val initialMediaIndex: Int,
        val media: ImmutableList<MediaItem>,
    ) : MediaViewUiState
    data object Loading : MediaViewUiState
}

internal sealed interface MediaViewEvent {
    data object OnButtonBackClick : MediaViewEvent
}

internal sealed interface MediaViewEffect {
    data object CloseScreen : MediaViewEffect
}
