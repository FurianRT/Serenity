package com.furianrt.settings.internal.ui

import androidx.annotation.IntRange
import com.furianrt.settings.internal.entities.AppTheme
import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val themes: ImmutableList<AppTheme>,
        val selectedThemeColor: UiThemeColor,
        @IntRange(0L, 5L) val rating: Int,
    ) : SettingsUiState
}

internal sealed interface SettingsEvent {
    data object OnButtonBackClick : SettingsEvent
    data object OnButtonSecurityClick : SettingsEvent
    data object OnButtonBackupClick : SettingsEvent
    data class OnAppThemeColorSelected(val color: UiThemeColor) : SettingsEvent
    data object OnButtonFeedbackClick : SettingsEvent
    data class OnRatingSelected(val rating: Int) : SettingsEvent
}

internal sealed interface SettingsEffect {
    data object CloseScreen : SettingsEffect
    data object OpenSecurityScreen : SettingsEffect
    data object OpenBackupScreen : SettingsEffect
    data class SendFeedbackEmail(
        val supportEmail: String,
        val androidVersion: String,
        val language: String,
        val device: String,
        val appVersion: String,
    ) : SettingsEffect {
        val text: String
            get() = "$device $androidVersion $language $appVersion"
    }

    data class OpenMarketPage(val url: String) : SettingsEffect
}