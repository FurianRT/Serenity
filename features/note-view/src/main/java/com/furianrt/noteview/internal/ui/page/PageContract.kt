package com.furianrt.noteview.internal.ui.page

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList

@Stable
internal sealed interface PageUiState {
    @Immutable
    object Loading : PageUiState

    @Immutable
    object Empty : PageUiState

    @Immutable
    data class Success(
        val content: ImmutableList<UiNoteContent>,
        val tags: ImmutableList<UiNoteTag>,
        val isInEditMode: Boolean,
    ) : PageUiState
}

@Stable
internal sealed interface PageEvent {
    @Immutable
    data class OnEditModeStateChange(val isEnabled: Boolean) : PageEvent

    @Immutable
    data class OnTagClick(val tag: UiNoteTag) : PageEvent

    @Immutable
    data class OnTagRemoved(val tag: UiNoteTag) : PageEvent

    @Immutable
    data class OnTitleTextChange(val id: String, val text: String) : PageEvent

    @Immutable
    data class OnTitleDoneEditing(val id: String, val text: String) : PageEvent

    @Immutable
    object OnTitleFocused : PageEvent
}

@Stable
internal sealed interface PageEffect {
    @Immutable
    data class FocusTitle(val index: Int?) : PageEffect
}
