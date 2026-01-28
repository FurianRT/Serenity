package com.furianrt.toolspanel.internal.ui.background.pattern

import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.uikit.entities.UiThemeColor

internal sealed interface PatternBackgroundUiState {
    data class Success(
        val images: List<UiNoteBackgroundImage>,
        val colors: List<UiNoteBackground>,
        val selectedImageIndex: Int?,
        val selectedColorIndex: Int?,
        val appTheme: UiThemeColor,
    ) : PatternBackgroundUiState

    data object Loading : PatternBackgroundUiState
}

internal sealed interface PatternBackgroundEvent {
    data class OnImageSelected(val image: UiNoteBackgroundImage) : PatternBackgroundEvent
    data class OnColorSelected(val color: UiNoteBackground) : PatternBackgroundEvent
    data object OnClearClick : PatternBackgroundEvent
}

internal sealed interface PatternBackgroundEffect {
    data class SendThemeSelected(val theme: UiNoteTheme.Image.Pattern?) : PatternBackgroundEffect
}