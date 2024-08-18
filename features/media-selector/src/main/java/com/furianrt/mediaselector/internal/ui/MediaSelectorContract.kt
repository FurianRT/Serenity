package com.furianrt.mediaselector.internal.ui

import androidx.compose.foundation.lazy.grid.LazyGridState
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import kotlinx.collections.immutable.ImmutableList


internal sealed interface MediaSelectorUiState {
    data object Loading : MediaSelectorUiState
    data object Empty : MediaSelectorUiState
    data class Success(
        val items: ImmutableList<MediaItem>,
        val listState: LazyGridState,
    ) : MediaSelectorUiState
}

internal sealed interface MediaSelectorEvent

internal sealed interface MediaSelectorEffect