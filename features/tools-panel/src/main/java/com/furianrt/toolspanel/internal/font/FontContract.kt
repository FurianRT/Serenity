package com.furianrt.toolspanel.internal.font

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class FontPanelUiState(
    val fontColors: ImmutableList<UiNoteFontColor>,
    val fontFamilies: ImmutableList<UiNoteFontFamily>,
    val selectedFontColor: UiNoteFontColor,
    val selectedFontFamily: UiNoteFontFamily,
    val selectedFontSize: Int,
)

internal sealed interface FontPanelEvent {
    data class OnFontColorSelected(val color: UiNoteFontColor) : FontPanelEvent
    data class OnFontFamilySelected(val family: UiNoteFontFamily) : FontPanelEvent
    data class OnFontSizeSelected(val size: Int) : FontPanelEvent
}
