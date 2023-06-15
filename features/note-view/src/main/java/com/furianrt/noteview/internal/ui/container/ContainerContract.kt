package com.furianrt.noteview.internal.ui.container

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
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
        val date: String,
        val notesIds: ImmutableList<String>,
    ) : ContainerUiState
}

@Stable
internal sealed interface ContainerEvent

@Stable
internal sealed interface ContainerEffect
