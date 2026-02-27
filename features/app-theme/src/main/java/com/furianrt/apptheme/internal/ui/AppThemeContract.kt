package com.furianrt.apptheme.internal.ui

import com.furianrt.uikit.entities.UiThemeColor

internal data class AppThemeState(
    val theme: UiThemeColor,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content
        data class Success(
            val themes: List<UiThemeColor>,
            val selectedId: String,
        ) : Content
    }
}

internal sealed interface AppThemeEvent {
    data class OnThemeClick(val theme: UiThemeColor) : AppThemeEvent
    data object OnBackClick : AppThemeEvent
}

internal sealed interface AppThemeEffect {
    data object CloseScreen : AppThemeEffect
}