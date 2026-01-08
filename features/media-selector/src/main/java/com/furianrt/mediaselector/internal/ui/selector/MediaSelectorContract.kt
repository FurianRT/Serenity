package com.furianrt.mediaselector.internal.ui.selector

import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState

internal sealed class MediaSelectorUiState(
    open val selectedAlbum: MediaAlbumItem?,
) {
    data object Loading : MediaSelectorUiState(selectedAlbum = null)
    data class Empty(
        override val selectedAlbum: MediaAlbumItem?,
        val showPartialAccessMessage: Boolean,
    ) : MediaSelectorUiState(selectedAlbum)

    data class Success(
        val items: List<MediaItem>,
        val selectedCount: Int,
        override val selectedAlbum: MediaAlbumItem?,
        val showPartialAccessMessage: Boolean,
    ) : MediaSelectorUiState(selectedAlbum) {

        fun setSelectedItems(selectedItems: List<MediaItem>): Success = copy(
            selectedCount = selectedItems.count(),
            items = items.map { item ->
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
    data object OnAlbumsClick : MediaSelectorEvent
    data class OnAlbumSelected(val album: MediaAlbumItem) : MediaSelectorEvent
    data object OnAlbumsDismissed : MediaSelectorEvent
}

internal sealed interface MediaSelectorEffect {
    data object RequestMediaPermissions : MediaSelectorEffect
    data object CloseScreen : MediaSelectorEffect
    data class ShowAlbumsList(val albums: List<MediaAlbumItem>) : MediaSelectorEffect
    data object HideAlbumsList : MediaSelectorEffect
    data class SendMediaResult(val result: MediaResult) : MediaSelectorEffect
    data class OpenMediaViewer(
        val dialogId: Int,
        val requestId: String,
        val mediaId: Long,
        val albumId: String?,
    ) : MediaSelectorEffect
}