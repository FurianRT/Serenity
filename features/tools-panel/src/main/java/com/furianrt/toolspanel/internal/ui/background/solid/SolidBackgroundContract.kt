package com.furianrt.toolspanel.internal.ui.background.solid

import com.furianrt.notelistui.entities.UiNoteTheme

internal data class SolidBackgroundUiState(
    val themes: List<UiNoteTheme.Solid>,
    val selectedThemeIndex: Int?,
)

internal sealed interface SolidBackgroundEvent {
    data class OnThemeSelected(val theme: UiNoteTheme.Solid) : SolidBackgroundEvent
    data object OnClearBackgroundClick : SolidBackgroundEvent
}

internal sealed interface SolidBackgroundEffect {
    data class OnThemeSelected(val theme: UiNoteTheme.Solid?) : SolidBackgroundEffect
}
