package com.furianrt.toolspanel.internal.ui.background.image

import com.furianrt.notelistui.entities.UiNoteTheme

internal data class ImageBackgroundUiState(
    val themes: List<UiNoteTheme.Image.Picture>,
    val selectedThemeIndex: Int?,
)

internal sealed interface ImageBackgroundEvent {
    data class OnThemeSelected(val theme: UiNoteTheme.Image.Picture) : ImageBackgroundEvent
    data object OnClearBackgroundClick : ImageBackgroundEvent
}

internal sealed interface ImageBackgroundEffect {
    data class OnThemeSelected(val theme: UiNoteTheme.Image.Picture?) : ImageBackgroundEffect
}