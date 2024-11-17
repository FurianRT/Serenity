package com.furianrt.settings.internal.ui.main

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data object Success : SettingsUiState
}

internal sealed interface SettingsEvent {
    data object OnButtonBackClick : SettingsEvent
    data object OnButtonSecurityClick : SettingsEvent
}

internal sealed interface SettingsEffect {
    data object CloseScreen : SettingsEffect
    data object OpenSecurityScreen : SettingsEffect
}