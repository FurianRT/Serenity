package com.furianrt.mediaview.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.uikit.theme.NoteFont

internal sealed interface MediaViewUiState {
    data object Loading : MediaViewUiState

    @Immutable
    data class Success(
        val media: List<MediaItem>,
        val font: NoteFont,
        val initialPage: Int,
        val showDeleteButton: Boolean,
        val showGoToNoteButton: Boolean,
    ) : MediaViewUiState
}

internal sealed interface MediaViewEvent {
    data object OnButtonBackClick : MediaViewEvent
    data class OnButtonDeleteClick(val mediaIndex: Int) : MediaViewEvent
    data class OnButtonSaveToGalleryClick(val mediaIndex: Int) : MediaViewEvent
    data class OnButtonShareClick(val mediaIndex: Int) : MediaViewEvent
    data class OnButtonGoToNoteClick(val mediaIndex: Int) : MediaViewEvent
}

internal sealed interface MediaViewEffect {
    data object CloseScreen : MediaViewEffect
    data object ShowMediaSavedMessage : MediaViewEffect
    data object ShowMediaSaveErrorMessage : MediaViewEffect
    data class ShowSyncProgressMessage(val message: String) : MediaViewEffect
    data class ShareMedia(val media: MediaItem) : MediaViewEffect
    data class OpenNoteViewScreen(val noteId: String) : MediaViewEffect
}
