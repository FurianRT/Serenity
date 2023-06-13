package com.furianrt.noteview.internal.ui.container

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.furianrt.noteview.internal.ui.container.entites.ContainerScreenNote
import kotlinx.collections.immutable.ImmutableList

@Stable
internal sealed interface ContainerUiState {
    @Immutable
    object Loading : ContainerUiState

    @Immutable
    object Empty : ContainerUiState

    @Immutable
    data class Success(
        val initialPageIndex: Int,
        val notes: ImmutableList<ContainerScreenNote>,
    ) : ContainerUiState
}

@Stable
internal sealed interface ContainerEvent

@Stable
internal sealed interface ContainerEffect
