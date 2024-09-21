package com.furianrt.serenity.ui

import androidx.compose.runtime.Immutable
import com.furianrt.serenity.ui.entities.MainScreenNote
import kotlinx.collections.immutable.ImmutableList

internal sealed interface MainUiState {
    data object Loading : MainUiState

    data object Empty : MainUiState

    @Immutable
    data class Success(
        val notes: ImmutableList<MainScreenNote>,
        val scrollToPosition: Int?,
    ) : MainUiState
}

internal val MainUiState.hasNotes
    get() = this is MainUiState.Success && notes.isNotEmpty()

internal sealed interface MainEvent {
    data class OnNoteClick(val note: MainScreenNote) : MainEvent
    data class OnNoteLongClick(val note: MainScreenNote) : MainEvent
    data object OnScrollToTopClick : MainEvent
    data object OnSettingsClick : MainEvent
    data object OnSearchClick : MainEvent
    data object OnAddNoteClick : MainEvent
    data object OnScrolledToItem : MainEvent
}

internal sealed interface MainEffect {
    data object ScrollToTop : MainEffect
    data object OpenSettingsScreen : MainEffect
    data class OpenNoteCreateScreen(
        val dialogId: Int,
        val requestId: String,
    ) : MainEffect

    data class OpenNoteViewScreen(
        val noteId: String,
        val dialogId: Int,
        val requestId: String,
    ) : MainEffect

}
