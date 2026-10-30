package com.furianrt.apptheme.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.uikit.entities.UiAppTheme

internal data class AppThemeState(
    val theme: UiAppTheme,
    val content: Content,
) {
    @Immutable
    sealed interface Content {
        data object Loading : Content
        data class Success(
            val themes: List<UiAppTheme>,
            val selectedId: String,
        ) : Content
    }
}

internal sealed interface AppThemeEvent {
    data class OnThemeClick(val theme: UiAppTheme) : AppThemeEvent
    data object OnBackClick : AppThemeEvent
}

internal sealed interface AppThemeEffect {
    data object CloseScreen : AppThemeEffect
}