package com.furianrt.serenity.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.furianrt.serenity.ui.entities.MainScreenNote
import kotlinx.collections.immutable.ImmutableList

@Stable
internal sealed interface MainUiState {
    @Immutable
    object Loading : MainUiState

    @Immutable
    object Empty : MainUiState

    @Immutable
    data class Success(val notes: ImmutableList<MainScreenNote>) : MainUiState
}

internal val MainUiState.hasNotes
    get() = this is MainUiState.Success && notes.isNotEmpty()

@Stable
internal sealed interface MainEvent {
    @Immutable
    object OnScrollToTopClick : MainEvent

    @Immutable
    object OnSettingsClick : MainEvent

    @Immutable
    object OnSearchClick : MainEvent

    @Immutable
    object OnAddNoteClick : MainEvent
}

@Stable
internal sealed interface MainEffect {
    @Immutable
    object ScrollToTop : MainEffect
}
