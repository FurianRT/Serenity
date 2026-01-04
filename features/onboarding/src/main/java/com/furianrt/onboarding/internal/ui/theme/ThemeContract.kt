package com.furianrt.onboarding.internal.ui.theme

import com.furianrt.uikit.entities.UiThemeColor

internal sealed interface ThemeState {
    data object Loading : ThemeState
    data class Success(
        val initialPageIndex: Int,
        val themes: List<UiThemeColor>,
        val tabs: List<String>,
    ) : ThemeState
}

internal sealed interface ThemeEvent {
    data class OnThemeTabClick(val theme: UiThemeColor) : ThemeEvent
}

internal sealed interface ThemeEffect {
    data class ScrollToTheme(val index: Int) : ThemeEffect
}
