package com.furianrt.toolspanel.internal.ui.font

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily

@Immutable
internal data class FontPanelUiState(
    val fontColors: List<UiNoteFontColor>,
    val fontFamilies: List<UiNoteFontFamily>,
    val selectedFontColor: UiNoteFontColor?,
    val selectedFontFamily: UiNoteFontFamily?,
    val selectedFontSize: Int,
    val defaultFontFamily: UiNoteFontFamily?,
)

internal sealed interface FontPanelEvent {
    data class OnFontColorSelected(val color: UiNoteFontColor?) : FontPanelEvent
    data class OnFontFamilySelected(val family: UiNoteFontFamily?) : FontPanelEvent
    data class OnFontSizeSelected(val size: Int) : FontPanelEvent
}
