package com.furianrt.notepage.internal.ui

import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.collections.immutable.ImmutableList

internal sealed interface PageUiState {
    data object Loading : PageUiState
    data object Empty : PageUiState
    data class Success(
        val noteId: String,
        val content: ImmutableList<UiNoteContent>,
        val tags: ImmutableList<UiNoteTag>,
        val fontFamily: UiNoteFontFamily,
        val fontColor: UiNoteFontColor,
        val isInEditMode: Boolean,
    ) : PageUiState {
        val isContentEmpty: Boolean
            get() = content.all { content ->
                content is UiNoteContent.Title && content.state.text.isEmpty()
            }
    }
}

internal sealed interface PageEvent {
    data class OnEditModeStateChange(val isEnabled: Boolean) : PageEvent
    data class OnTagRemoveClick(val tag: UiNoteTag.Regular) : PageEvent
    data class OnTagDoneEditing(val tag: UiNoteTag.Template) : PageEvent
    data object OnTagTextEntered : PageEvent
    data object OnTagTextCleared : PageEvent
    data object OnSelectMediaClick : PageEvent
    data object OnMediaPermissionsSelected : PageEvent
    data class OnTitleFocusChange(val id: String) : PageEvent
    data class OnMediaClick(val media: UiNoteContent.MediaBlock.Media) : PageEvent
    data class OnMediaRemoveClick(val media: UiNoteContent.MediaBlock.Media) : PageEvent
    data class OnMediaShareClick(val media: UiNoteContent.MediaBlock.Media) : PageEvent
    data class OnMediaSelected(val result: MediaResult) : PageEvent
    data class OnOpenMediaViewerRequest(val route: MediaViewerRoute) : PageEvent
    data class OnTitleTextChange(val id: String) : PageEvent
    data object OnOnSaveContentRequest : PageEvent
    data class OnFontFamilySelected(val family: UiNoteFontFamily) : PageEvent
    data class OnFontColorSelected(val color: UiNoteFontColor) : PageEvent
}

internal sealed interface PageEffect {
    data object RequestStoragePermissions : PageEffect
    data object ShowPermissionsDeniedDialog : PageEffect
    data class FocusFirstTitle(val index: Int) : PageEffect
    data object OpenMediaSelector : PageEffect
    data class OpenMediaViewer(val route: MediaViewerRoute) : PageEffect
    data class UpdateContentChangedState(val isChanged: Boolean) : PageEffect
    data class OpenMediaViewScreen(
        val noteId: String,
        val mediaName: String,
        val identifier: DialogIdentifier,
    ) : PageEffect
}
