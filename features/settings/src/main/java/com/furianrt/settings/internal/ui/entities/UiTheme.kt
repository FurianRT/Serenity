package com.furianrt.settings.internal.ui.entities

import com.furianrt.uikit.entities.UiThemeColor

internal sealed class UiTheme(
    open val isSelected: Boolean,
    open val colors: List<UiThemeColor>,
    open val selectedColor: UiThemeColor,
) {
    data class Light(
        override val isSelected: Boolean,
        override val colors: List<UiThemeColor>,
        override val selectedColor: UiThemeColor,
    ) : UiTheme(isSelected, colors, selectedColor)

    data class Dark(
        override val isSelected: Boolean,
        override val colors: List<UiThemeColor>,
        override val selectedColor: UiThemeColor,
    ) : UiTheme(isSelected, colors, selectedColor)
}