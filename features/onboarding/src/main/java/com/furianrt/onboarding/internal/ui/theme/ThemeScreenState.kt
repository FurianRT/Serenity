package com.furianrt.onboarding.internal.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.furianrt.uikit.entities.UiAppTheme

internal class ThemeScreenState(
    initialTheme: UiAppTheme,
) {
    var selectedTheme: UiAppTheme by mutableStateOf(initialTheme)
}