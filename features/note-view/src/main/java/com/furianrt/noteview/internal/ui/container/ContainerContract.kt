package com.furianrt.noteview.internal.ui.container

import com.furianrt.noteview.internal.ui.entites.ContainerScreenNote
import kotlinx.collections.immutable.ImmutableList

internal sealed interface ContainerUiState {
    data object Loading : ContainerUiState
    data class Success(
        val initialPageIndex: Int,
        val isInEditMode: Boolean,
        val notes: ImmutableList<ContainerScreenNote>,
    ) : ContainerUiState
}

internal sealed interface ContainerEvent {
    data object OnButtonEditClick : ContainerEvent
    data class OnButtonBackClick(val isContentSaved: Boolean) : ContainerEvent
    data object OnPageTitleFocusChange : ContainerEvent
    data class OnPageChange(val index: Int) : ContainerEvent
}

internal sealed interface ContainerEffect {
    data object CloseScreen : ContainerEffect
    data object SaveCurrentNoteContent : ContainerEffect
}
