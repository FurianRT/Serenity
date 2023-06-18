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

    @Stable
    sealed class Success(
        open val content: ImmutableList<UiNoteContent>,
        open val tags: ImmutableList<UiNoteTag>,
    ) : PageUiState {
        @Immutable
        data class Edit(
            override val content: ImmutableList<UiNoteContent>,
            override val tags: ImmutableList<UiNoteTag>,
        ) : Success(content, tags)

        @Immutable
        data class View(
            override val content: ImmutableList<UiNoteContent>,
            override val tags: ImmutableList<UiNoteTag>,
        ) : Success(content, tags)
    }
}

@Stable
internal sealed interface PageEvent {
    @Immutable
    data class OnEditModeStateChange(val isEnabled: Boolean) : PageEvent

    @Immutable
    data class OnTagClick(val tag: UiNoteTag) : PageEvent

    @Immutable
    data class OnTagRemoved(val tag: UiNoteTag) : PageEvent
}

@Stable
internal sealed interface PageEffect
