package com.furianrt.noteview.internal.ui.container

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.furianrt.noteview.internal.ui.entites.ContainerScreenNote
import kotlinx.collections.immutable.ImmutableList

@Stable
internal sealed interface ContainerUiState {
    @Immutable
    data object Loading : ContainerUiState

    @Immutable
    data object Empty : ContainerUiState

    @Immutable
    data class Success(
        val initialPageIndex: Int,
        val isInEditMode: Boolean,
        val notes: ImmutableList<ContainerScreenNote>,
    ) : ContainerUiState
}

@Stable
internal sealed interface ContainerEvent {
    @Immutable
    data object OnButtonEditClick : ContainerEvent

    @Immutable
    data object OnButtonBackClick : ContainerEvent

    @Immutable
    data object OnPageTitleFocusChange : ContainerEvent
}

@Stable
internal sealed interface ContainerEffect {
    @Immutable
    data object CloseScreen : ContainerEffect
}
