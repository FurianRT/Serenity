package com.furianrt.noteview.internal.ui.page

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
internal sealed interface PageUiState {
    @Immutable
    object Loading : PageUiState

    @Immutable
    object Empty : PageUiState

    @Immutable
    object Success : PageUiState
}

@Stable
internal sealed interface PageEvent

@Stable
internal sealed interface PageEffect
