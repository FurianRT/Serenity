package com.furianrt.mediasorting.internal.ui

import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.mediasorting.internal.ui.entities.MediaItem
import com.furianrt.uikit.utils.DialogIdentifier

internal data class MediaSortingUiState(
    val media: List<MediaItem>,
    val hasContentChanged: Boolean,
)

internal sealed interface MediaSortingEvent {
    data class OnRemoveMediaClick(val media: MediaItem) : MediaSortingEvent
    data object OnAddMediaClick : MediaSortingEvent
    data object OnButtonBackClick : MediaSortingEvent
    data object OnConfirmCloseClick : MediaSortingEvent
    data object OnButtonDoneClick : MediaSortingEvent
    data class OnMediaClick(val media: MediaItem) : MediaSortingEvent
    data class OnMediaItemMoved(
        val from: LazyGridItemInfo,
        val to: LazyGridItemInfo,
    ) : MediaSortingEvent

    data class OnOpenMediaViewerRequest(val route: MediaViewerRoute) : MediaSortingEvent
    data class OnMediaSelected(val result: MediaResult) : MediaSortingEvent
    data object OnMediaPermissionsSelected : MediaSortingEvent
}

internal sealed interface MediaSortingEffect {
    data object CloseScreen : MediaSortingEffect
    data object ShowConfirmCloseDialog : MediaSortingEffect
    data object RequestStoragePermissions : MediaSortingEffect
    data object ShowPermissionsDeniedDialog : MediaSortingEffect
    data object OpenMediaSelector : MediaSortingEffect
    data class OpenMediaViewer(val route: MediaViewerRoute) : MediaSortingEffect
    data class OpenMediaViewScreen(
        val noteId: String,
        val mediaBlockId: String,
        val mediaId: String,
        val identifier: DialogIdentifier,
    ) : MediaSortingEffect
}
