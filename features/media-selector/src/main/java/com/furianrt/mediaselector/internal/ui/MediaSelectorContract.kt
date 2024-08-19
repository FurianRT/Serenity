package com.furianrt.mediaselector.internal.ui

import androidx.compose.foundation.lazy.grid.LazyGridState
import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.storage.api.repositories.DeviceMediaRepository
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
        ) : ScreenState {
            fun toggleSelection(itemId: Long): Success = copy(
                items = items.mapImmutable { item ->
                    if (item.id == itemId) {
                        item.toggleSelection()
                    } else {
                        item
                    }
                },
            )
        }
    }

    val mediaPermissionsList: List<String>
        get() = DeviceMediaRepository.getMediaPermissionList()
}

internal sealed interface MediaSelectorEvent {
    data object OnPartialAccessMessageClick : MediaSelectorEvent
    data object OnMediaPermissionsSelected : MediaSelectorEvent
    data class OnSelectItemClick(val itemId: Long) : MediaSelectorEvent
}

internal sealed interface MediaSelectorEffect {
    data object RequestMediaPermissions : MediaSelectorEffect
}