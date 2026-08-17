package com.furianrt.onboarding.internal.ui.theme

import androidx.compose.runtime.Immutable
import com.furianrt.uikit.entities.UiThemeColor

internal data class ThemeState(
    val theme: UiThemeColor,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content

        @Immutable
        data class Success(
            val initialPageIndex: Int,
            val themes: List<UiThemeColor>,
        ) : Content
    }
}
