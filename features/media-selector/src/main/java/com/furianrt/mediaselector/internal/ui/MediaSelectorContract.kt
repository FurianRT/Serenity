package com.furianrt.mediaselector.internal.ui

import androidx.compose.foundation.lazy.grid.LazyGridState
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import kotlinx.collections.immutable.ImmutableList

internal data class MediaSelectorUiState(
    val showPartialAccessMessage: Boolean,
    val screenState: ScreenState,
) {
    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Empty : ScreenState
        data class Success(
            val items: ImmutableList<MediaItem>,
            val listState: LazyGridState = LazyGridState(),
        ) : ScreenState
    }
}

internal sealed interface MediaSelectorEvent {
    data object OnPartialAccessMessageClick : MediaSelectorEvent
    data object OnMediaPermissionsSelected : MediaSelectorEvent
}

internal sealed interface MediaSelectorEffect {
    data object RequestMediaPermission : MediaSelectorEffect
}