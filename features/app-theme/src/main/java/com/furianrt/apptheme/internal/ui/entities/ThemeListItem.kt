package com.furianrt.apptheme.internal.ui.entities

import com.furianrt.uikit.entities.UiAppTheme

internal sealed class ThemeListItem(
    open val id: String,
) {
    data class CustomBackground(
        val theme: UiAppTheme.Custom,
    ) : ThemeListItem(
        id = theme.id,
    )

    data class RegularBackground(
        val theme: UiAppTheme.Regular,
    ) : ThemeListItem(
        id = theme.id,
    )

    data object Hint : ThemeListItem(
        id = "hint",
    )
}