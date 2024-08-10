package com.furianrt.noteview.internal.ui.page

import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

internal sealed interface PageUiState {
    data object Loading : PageUiState
    data object Empty : PageUiState
    data class Success(
        val content: ImmutableList<UiNoteContent>,
        val tags: ImmutableList<UiNoteTag>,
        val isInEditMode: Boolean,
    ) : PageUiState
}

internal sealed interface PageEvent {
    data class OnEditModeStateChange(val isEnabled: Boolean) : PageEvent
    data class OnTagClick(val tag: UiNoteTag) : PageEvent
    data class OnTagRemoved(val tag: UiNoteTag) : PageEvent
    data class OnTitleTextChange(val id: String, val text: String) : PageEvent
    data class OnTitleDoneEditing(val id: String, val text: String) : PageEvent
}

internal sealed interface PageEffect
