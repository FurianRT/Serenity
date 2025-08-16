package com.furianrt.toolspanel.internal.ui.background

import com.furianrt.notelistui.entities.UiNoteBackground

internal class BackgroundPanelUiState(
    val tabs: List<Tab> = emptyList(),
    val selectedTabIndex: Int = 0,
    val selectedBackground: UiNoteBackground? = null,
) {
    sealed class Tab(
        open val items: List<UiNoteBackground>,
    ) {
        data class All(
            override val items: List<UiNoteBackground>,
        ) : Tab(items)

        data class Dark(
            override val items: List<UiNoteBackground>,
        ) : Tab(items)

        data class Light(
            override val items: List<UiNoteBackground>,
        ) : Tab(items)
    }
}

internal sealed interface BackgroundPanelEvent {
    data object OnCloseClick : BackgroundPanelEvent
    data object OnKeyboardClick : BackgroundPanelEvent
    data class OnTitleTabClick(val index: Int) : BackgroundPanelEvent
    data class OnBackgroundSelected(val background: UiNoteBackground) : BackgroundPanelEvent
    data class OnContentPageChange(val index: Int) : BackgroundPanelEvent
    data object OnClearBackgroundClick: BackgroundPanelEvent
}

internal sealed interface BackgroundPanelEffect {
    data object ClosePanel : BackgroundPanelEffect
    data object ShowKeyboard : BackgroundPanelEffect
    data class ScrollContentToIndex(val index: Int) : BackgroundPanelEffect
    data class SelectBackground(val item: UiNoteBackground?) : BackgroundPanelEffect
}
