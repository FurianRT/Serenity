package com.furianrt.setiings.internal.ui

internal sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data object Success : SettingsUiState
}

internal sealed interface SettingsEvent {
    data object OnButtonBackClick : SettingsEvent
}

internal sealed interface SettingsEffect {
    data object CloseScreen : SettingsEffect
}