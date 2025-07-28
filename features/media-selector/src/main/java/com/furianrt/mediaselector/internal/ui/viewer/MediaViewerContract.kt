package com.furianrt.mediaselector.internal.ui.viewer

import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import kotlinx.collections.immutable.ImmutableList

internal sealed interface MediaViewerUiState {
    data class Success(
        val initialMediaIndex: Int,
        val media: ImmutableList<MediaItem>,
        val isLightTheme: Boolean,
    ) : MediaViewerUiState {
        fun setSelectedItems(selectedItems: List<MediaItem>): Success = copy(
            media = media.mapImmutable { item ->
                val selectedIndex = selectedItems.indexOfFirst { it.id == item.id }
                when {
                    selectedIndex != -1 -> {
                        item.changeState(SelectionState.Selected(order = selectedIndex + 1))
                    }

                    item.state is SelectionState.Selected -> {
                        item.changeState(SelectionState.Default)
                    }

                    else -> item
                }
            },
        )
    }

    data object Loading : MediaViewerUiState
}

internal sealed interface MediaViewerEvent {
    data object OnButtonBackClick : MediaViewerEvent
    data class OnMediaSelectionToggle(val media: MediaItem) : MediaViewerEvent
}

internal sealed interface MediaViewerEffect {
    data object CloseScreen : MediaViewerEffect
}