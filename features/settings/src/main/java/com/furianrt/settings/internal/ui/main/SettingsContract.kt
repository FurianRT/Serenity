package com.furianrt.settings.internal.ui.main

import androidx.compose.ui.graphics.Color
import com.furianrt.settings.internal.ui.main.SettingsUiState.Success.AppThemeColor
import com.furianrt.uikit.theme.Colors
import kotlinx.collections.immutable.ImmutableList

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val themeColors: ImmutableList<AppThemeColor>,
        val selectedThemeColor: AppThemeColor,
    ) : SettingsUiState {
        enum class AppThemeColor(val value: Color) {
            GREEN(Colors.Green),
            BLACK(Color.Black),
            BLUE(Colors.FutureDusk);
        }
    }
}

internal sealed interface SettingsEvent {
    data object OnButtonBackClick : SettingsEvent
    data object OnButtonSecurityClick : SettingsEvent
    data class OnAppThemeColorSelected(val color: AppThemeColor) : SettingsEvent
}

internal sealed interface SettingsEffect {
    data object CloseScreen : SettingsEffect
    data object OpenSecurityScreen : SettingsEffect
}