package com.furianrt.toolspanel.internal.ui.font

import androidx.compose.runtime.Immutable
import com.furianrt.domain.entities.NoteTextAlignment
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.internal.ui.color.ColorPickerState

internal data class FontPanelUiState(
    val content: Content,
) {
    sealed interface Content {

        @Immutable
        data class Main(
            val fontColors: List<UiNoteFontColor>,
            val fontFamilies: List<UiNoteFontFamily>,
            val selectedFontColor: SelectedFontColor,
            val selectedFontFamily: UiNoteFontFamily?,
            val selectedFontSize: Int,
            val defaultFontFamily: UiNoteFontFamily?,
            val textAlignment: NoteTextAlignment,
        ) : Content

        @Immutable
        data class ColorPicker(
            val pickerState: ColorPickerState,
        ) : Content
    }

    sealed interface SelectedFontColor {
        data class Predefined(
            val fontColor: UiNoteFontColor,
        ) : SelectedFontColor

        data object None : SelectedFontColor

        val predefinedColor: UiNoteFontColor?
            get() = if (this is Predefined) fontColor else null
    }
}

internal sealed interface FontPanelEvent {
    data object OnCloseCustomFontSelectorClick : FontPanelEvent
    data object OnCustomFontColorClick : FontPanelEvent
    data class OnFontColorSelected(val color: UiNoteFontColor?) : FontPanelEvent
    data class OnFontFamilySelected(val family: UiNoteFontFamily?) : FontPanelEvent
    data class OnFontSizeSelected(val size: Int) : FontPanelEvent
    data object OnTextAlignmentClick : FontPanelEvent
    data object OnFontSendFeedbackClick : FontPanelEvent
    data object OnPanelClosed : FontPanelEvent
}

internal sealed interface FontPanelEffect {
    data class SendFeedbackEmail(
        val supportEmail: String,
        val text: String,
    ) : FontPanelEffect

    data class SendAlignmentChangeAction(val alignment: NoteTextAlignment) : FontPanelEffect
}
