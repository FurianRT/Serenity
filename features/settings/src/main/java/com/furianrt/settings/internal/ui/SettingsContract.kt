package com.furianrt.settings.internal.ui

import com.furianrt.settings.internal.entities.AppTheme
import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val themes: ImmutableList<AppTheme>,
        val selectedThemeColor: UiThemeColor,
    ) : SettingsUiState
}

internal sealed interface SettingsEvent {
    data object OnButtonBackClick : SettingsEvent
    data object OnButtonSecurityClick : SettingsEvent
    data object OnButtonBackupClick : SettingsEvent
    data class OnAppThemeColorSelected(val color: UiThemeColor) : SettingsEvent
    data object OnButtonFeedbackClick : SettingsEvent
}

internal sealed interface SettingsEffect {
    data object CloseScreen : SettingsEffect
    data object OpenSecurityScreen : SettingsEffect
    data object OpenBackupScreen : SettingsEffect
    data class SendFeedbackEmail(
        val supportEmail: String,
        val androidVersion: Int,
        val language: String,
        val device: String,
    ) : SettingsEffect {
        val text: String
            get() = "$device $androidVersion $language"
    }
}