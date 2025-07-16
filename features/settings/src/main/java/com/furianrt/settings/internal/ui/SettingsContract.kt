package com.furianrt.settings.internal.ui

import androidx.annotation.IntRange
import com.furianrt.domain.entities.AppLocale
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val themes: ImmutableList<UiThemeColor>,
        val selectedThemeColor: UiThemeColor,
        @param:IntRange(0L, 5L) val rating: Int,
        val appVersion: String,
        val locale: AppLocale,
    ) : SettingsUiState
}

internal sealed interface SettingsEvent {
    data object OnButtonBackClick : SettingsEvent
    data object OnButtonSecurityClick : SettingsEvent
    data object OnButtonBackupClick : SettingsEvent
    data object OnButtonFontClick : SettingsEvent
    data class OnFontSelected(val font: UiNoteFontFamily) : SettingsEvent
    data class OnAppThemeColorSelected(val color: UiThemeColor) : SettingsEvent
    data object OnButtonFeedbackClick : SettingsEvent
    data class OnRatingSelected(val rating: Int) : SettingsEvent
    data object OnLocaleClick : SettingsEvent
    data class OnLocaleSelected(val locale: AppLocale) : SettingsEvent
    data object OnButtonTermsAndConditionsClick : SettingsEvent
    data object OnButtonPrivacyPolicyClick : SettingsEvent
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
    data object ShowBadRatingDialog : SettingsEffect
    data class ShowFontDialog(
        val fonts: ImmutableList<UiNoteFontFamily>,
        val selectedFont: UiNoteFontFamily,
    ) : SettingsEffect

    data class OpenLink(val url: String) : SettingsEffect
    data class ShowLocaleDialog(
        val locale: List<AppLocale>,
        val selectedLocale: AppLocale,
    ) : SettingsEffect
}