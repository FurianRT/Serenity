package com.furianrt.mediaselector.internal.ui

import androidx.compose.foundation.lazy.grid.LazyGridState
import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.storage.api.repositories.DeviceMediaRepository
import kotlinx.collections.immutable.ImmutableList

internal sealed interface MediaSelectorUiState {
    data object Loading : MediaSelectorUiState
    data class Empty(val showPartialAccessMessage: Boolean) : MediaSelectorUiState
    data class Success(
        val items: ImmutableList<MediaItem>,
        val showPartialAccessMessage: Boolean,
        val listState: LazyGridState = LazyGridState(),
    ) : MediaSelectorUiState {

        fun setSelectedItems(selectedItems: List<Long>): Success = copy(
            items = items.mapImmutable { item ->
                val selectedIndex = selectedItems.indexOfFirst { it == item.id }
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

    val mediaPermissionsList: List<String>
        get() = DeviceMediaRepository.getMediaPermissionList()
}

internal sealed interface MediaSelectorEvent {
    data object OnPartialAccessMessageClick : MediaSelectorEvent
    data object OnMediaPermissionsSelected : MediaSelectorEvent
    data class OnSelectItemClick(val item: MediaItem) : MediaSelectorEvent
}

internal sealed interface MediaSelectorEffect {
    data object RequestMediaPermissions : MediaSelectorEffect
    data object CloseScreen : MediaSelectorEffect
}