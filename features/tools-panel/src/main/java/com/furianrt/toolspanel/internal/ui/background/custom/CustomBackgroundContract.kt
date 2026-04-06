package com.furianrt.toolspanel.internal.ui.background.custom

import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.notelistui.entities.UiNoteTheme

internal sealed interface CustomBackgroundUiState {
    data object Loading : CustomBackgroundUiState
    data object Empty : CustomBackgroundUiState
    data class Content(
        val themes: List<UiNoteTheme.Image.Picture>,
        val selectedThemeIndex: Int?,
    ) : CustomBackgroundUiState
}

internal sealed interface CustomBackgroundEvent {
    data object OnSelectImageClick : CustomBackgroundEvent
    data class OnThemeSelected(val theme: UiNoteTheme.Image.Picture) : CustomBackgroundEvent
    data class OnDeleteThemeClick(val theme: UiNoteTheme.Image.Picture) : CustomBackgroundEvent
    data object OnClearBackgroundClick : CustomBackgroundEvent
}

internal sealed interface CustomBackgroundEffect {
    data class OnThemeSelected(val theme: UiNoteTheme.Image.Picture?) : CustomBackgroundEffect
    data class OpenMediaSelector(val params: MediaSelectorState.Params) : CustomBackgroundEffect
}
