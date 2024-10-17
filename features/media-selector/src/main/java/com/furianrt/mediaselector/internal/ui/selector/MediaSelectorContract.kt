package com.furianrt.mediaselector.internal.ui.selector

import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import kotlinx.collections.immutable.ImmutableList

internal sealed interface MediaSelectorUiState {
    data object Loading : MediaSelectorUiState
    data class Empty(val showPartialAccessMessage: Boolean) : MediaSelectorUiState
    data class Success(
        val items: ImmutableList<MediaItem>,
        val selectedCount: Int,
        val showPartialAccessMessage: Boolean,
    ) : MediaSelectorUiState {

        fun setSelectedItems(selectedItems: List<MediaItem>): Success = copy(
            selectedCount = selectedItems.count(),
            items = items.mapImmutable { item ->
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
}

internal sealed interface MediaSelectorEvent {
    data object OnPartialAccessMessageClick : MediaSelectorEvent
    data object OnMediaPermissionsSelected : MediaSelectorEvent
    data class OnSelectItemClick(val item: MediaItem) : MediaSelectorEvent
    data object OnSendClick : MediaSelectorEvent
    data class OnMediaClick(val id: Long) : MediaSelectorEvent
    data object OnCloseScreenRequest : MediaSelectorEvent
    data object OnExpanded : MediaSelectorEvent
    data object OnScreenResumed : MediaSelectorEvent
}

internal sealed interface MediaSelectorEffect {
    data object RequestMediaPermissions : MediaSelectorEffect
    data object CloseScreen : MediaSelectorEffect
    data class SendMediaResult(val result: MediaResult) : MediaSelectorEffect
    data class OpenMediaViewer(
        val dialogId: Int,
        val requestId: String,
        val mediaId: Long,
    ) : MediaSelectorEffect
}