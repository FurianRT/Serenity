package com.furianrt.mediaselector.internal.ui.viewer

import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState

internal sealed interface MediaViewerUiState {
    data class Success(
        val initialMediaIndex: Int,
        val media: List<MediaItem>,
    ) : MediaViewerUiState {
        fun setSelectedItems(
            selectedItems: List<MediaItem>,
            useCounter: Boolean,
        ): Success = copy(
            media = media.map { item ->
                val selectedIndex = selectedItems.indexOfFirst { it.id == item.id }
                when {
                    selectedIndex != -1 -> if (useCounter) {
                        item.changeState(SelectionState.Counter(order = selectedIndex + 1))
                    } else {
                        item.changeState(SelectionState.Single)
                    }

                    item.state is SelectionState.Counter || item.state is SelectionState.Single -> {
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