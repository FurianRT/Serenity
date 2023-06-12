package com.furianrt.noteview.internal.ui.container

import androidx.compose.runtime.Immutable
import com.furianrt.notecontent.entities.UiNote

@Immutable
internal sealed interface ContainerUiState {
    object Loading : ContainerUiState
    object Empty : ContainerUiState
    data class Success(
        val initialPageIndex: Int,
        val notes: List<UiNote>,
    ) : ContainerUiState
}

@Immutable
internal sealed interface ContainerEvent

@Immutable
internal sealed interface ContainerEffect
