package com.furianrt.mediaview.internal.ui

import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.uikit.theme.NoteFont
import kotlinx.collections.immutable.ImmutableList

internal data class MediaViewUiState(
    val initialMediaIndex: Int,
    val media: ImmutableList<MediaItem>,
    val font: NoteFont? = null,
)

internal sealed interface MediaViewEvent {
    data object OnButtonBackClick : MediaViewEvent
    data class OnButtonDeleteClick(val mediaIndex: Int) : MediaViewEvent
    data class OnButtonSaveToGalleryClick(val mediaIndex: Int) : MediaViewEvent
    data class OnButtonShareClick(val mediaIndex: Int) : MediaViewEvent
}

internal sealed interface MediaViewEffect {
    data object CloseScreen : MediaViewEffect
    data object ShowMediaSavedMessage : MediaViewEffect
    data object ShowMediaSaveErrorMessage : MediaViewEffect
    data class ShowSyncProgressMessage(val message: String) : MediaViewEffect
    data class ShareMedia(val media: MediaItem) : MediaViewEffect
}
