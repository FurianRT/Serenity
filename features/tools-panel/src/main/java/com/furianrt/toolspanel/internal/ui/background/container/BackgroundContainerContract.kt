package com.furianrt.toolspanel.internal.ui.background.container

import com.furianrt.notelistui.entities.UiNoteTheme

internal sealed interface BackgroundContainerUiState {
    data class Success(
        val noteId: String,
        val tabs: List<Tab>,
        val selectedTabIndex: Int,
        val selectedTheme: UiNoteTheme?,
        val selectedThemeProvider: BackgroundSelectedThemeProvider,
    ) : BackgroundContainerUiState {
        sealed interface Tab {
            data object Solid : Tab
            data object Picture : Tab
            data object Pattern : Tab
        }
    }

    data object Loading : BackgroundContainerUiState
}

internal sealed interface BackgroundContainerEvent {
    data object OnCloseClick : BackgroundContainerEvent
    data object OnKeyboardClick : BackgroundContainerEvent
    data class OnTitleTabClick(val index: Int) : BackgroundContainerEvent
    data class OnThemeSelected(val theme: UiNoteTheme?) : BackgroundContainerEvent
    data class OnContentPageChange(val index: Int) : BackgroundContainerEvent
}

internal sealed interface BackgroundContainerEffect {
    data object ClosePanel : BackgroundContainerEffect
    data object ShowKeyboard : BackgroundContainerEffect
    data class ScrollToPage(val index: Int) : BackgroundContainerEffect
}