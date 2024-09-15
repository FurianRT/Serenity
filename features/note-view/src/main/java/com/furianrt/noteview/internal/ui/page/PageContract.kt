package com.furianrt.noteview.internal.ui.page

import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.storage.api.repositories.MediaRepository
import kotlinx.collections.immutable.ImmutableList

internal sealed interface PageUiState {
    data object Loading : PageUiState
    data object Empty : PageUiState
    data class Success(
        val content: ImmutableList<UiNoteContent>,
        val tags: ImmutableList<UiNoteTag>,
        val isInEditMode: Boolean,
    ) : PageUiState

    val mediaPermissionsList: List<String>
        get() = MediaRepository.getMediaPermissionList()
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
    data class OnTitleTextChange(val id: String) : PageEvent
    data object OnOnSaveContentRequest : PageEvent
}

internal sealed interface PageEffect {
    data object RequestStoragePermissions : PageEffect
    data object ShowPermissionsDeniedDialog : PageEffect
    data class OpenMediaSelector(val dialogId: Int, val requestId: String): PageEffect
    data class UpdateContentChangedState(val isChanged: Boolean): PageEffect
}
