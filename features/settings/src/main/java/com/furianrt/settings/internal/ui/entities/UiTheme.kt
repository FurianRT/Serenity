package com.furianrt.settings.internal.ui.entities

import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList

internal sealed class UiTheme(
    open val isSelected: Boolean,
    open val colors: ImmutableList<UiThemeColor>,
    open val selectedColor: UiThemeColor,
) {
    data class Light(
        override val isSelected: Boolean,
        override val colors: ImmutableList<UiThemeColor>,
        override val selectedColor: UiThemeColor,
    ) : UiTheme(isSelected, colors, selectedColor)

    data class Dark(
        override val isSelected: Boolean,
        override val colors: ImmutableList<UiThemeColor>,
        override val selectedColor: UiThemeColor,
    ) : UiTheme(isSelected, colors, selectedColor)
}