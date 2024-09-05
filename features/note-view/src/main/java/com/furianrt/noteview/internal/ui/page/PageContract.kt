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
}

internal sealed interface PageEffect {
    data object RequestStoragePermissions : PageEffect
    data object ShowPermissionsDeniedDialog : PageEffect
    data object OpenMediaSelector : PageEffect
}
