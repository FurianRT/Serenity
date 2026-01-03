package com.furianrt.onboarding.internal.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.furianrt.uikit.entities.UiThemeColor

internal class ThemeScreenState(
    initialTheme: UiThemeColor,
) {
    var selectedTheme: UiThemeColor by mutableStateOf(initialTheme)
}