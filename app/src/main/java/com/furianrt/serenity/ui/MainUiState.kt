package com.furianrt.serenity.ui

import com.furianrt.uikit.entities.UiNote

sealed interface MainUiState {
    object Loading : MainUiState
    object Empty : MainUiState
    data class Success(val notes: List<UiNote>) : MainUiState
}

val MainUiState.hasNotes
    get() = this is MainUiState.Success && notes.isNotEmpty()

sealed interface MainEvent {
    object OnScrollToTopClick : MainEvent
    object OnSettingsClick : MainEvent
    object OnSearchClick : MainEvent
    object OnAddNoteClick : MainEvent
}

sealed interface MainEffect {
    object ScrollToTop : MainEffect
}
