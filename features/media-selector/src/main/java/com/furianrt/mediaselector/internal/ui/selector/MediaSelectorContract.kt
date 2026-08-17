package com.furianrt.mediaselector.internal.ui.selector

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState

internal sealed class MediaSelectorUiState(
    open val selectedAlbum: MediaAlbumItem?,
) {
    data object Loading : MediaSelectorUiState(
        selectedAlbum = null,
    )

    data object Hidden : MediaSelectorUiState(
        selectedAlbum = null,
    )

    @Immutable
    data class Success(
        val items: List<MediaItem>,
        val selectedCount: Int,
        val allowVideo: Boolean,
        override val selectedAlbum: MediaAlbumItem?,
        val showPartialAccessMessage: Boolean,
    ) : MediaSelectorUiState(selectedAlbum) {

        fun setSelectedItems(
            selectedItems: List<MediaItem>,
            useCounter: Boolean,
        ): Success = copy(
            selectedCount = selectedItems.count(),
            items = items.map { item ->
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

    val hasSelectedItems: Boolean
        get() = when (this) {
            is Loading, is Hidden -> false
            is Success -> selectedCount > 0
        }
}

internal sealed interface MediaSelectorEvent {
    data object OnPartialAccessMessageClick : MediaSelectorEvent
    data object OnMediaPermissionsSelected : MediaSelectorEvent
    data class OnSelectItemClick(val item: MediaItem) : MediaSelectorEvent
    data object OnSendClick : MediaSelectorEvent
    data class OnMediaClick(val id: Long) : MediaSelectorEvent
    data object OnCloseScreenRequest : MediaSelectorEvent
    data object OnScreenClosed : MediaSelectorEvent
    data class OnExpanded(val params: MediaSelectorState.Params?) : MediaSelectorEvent
    data object OnScreenResumed : MediaSelectorEvent
    data object OnAlbumsClick : MediaSelectorEvent
    data class OnAlbumSelected(val album: MediaAlbumItem) : MediaSelectorEvent
    data object OnAlbumsDismissed : MediaSelectorEvent
    data object OnCameraPhotoItemClick : MediaSelectorEvent
    data object OnCameraVideoItemClick : MediaSelectorEvent
    data object OnCameraPermissionSelected : MediaSelectorEvent
    data class OnTakePictureResult(val isSuccess: Boolean) : MediaSelectorEvent
    data class OnTakeVideoResult(val isSuccess: Boolean) : MediaSelectorEvent
    data class OnCameraNotFoundError(val error: Throwable) : MediaSelectorEvent
}

internal sealed interface MediaSelectorEffect {
    data object RequestMediaPermissions : MediaSelectorEffect
    data object CloseScreen : MediaSelectorEffect
    data class ShowAlbumsList(val albums: List<MediaAlbumItem>) : MediaSelectorEffect
    data object HideAlbumsList : MediaSelectorEffect
    data class OpenMediaViewer(
        val dialogId: Int,
        val requestId: String,
        val mediaId: Long,
        val albumId: String?,
        val singleChoice: Boolean,
        val allowVideo: Boolean,
    ) : MediaSelectorEffect

    data object RequestCameraPermission : MediaSelectorEffect
    data object ShowCameraPermissionsDeniedDialog : MediaSelectorEffect
    data class TakePicture(val uri: Uri) : MediaSelectorEffect
    data class TakeVideo(val uri: Uri) : MediaSelectorEffect
    data class ShowMessage(val text: String) : MediaSelectorEffect
}