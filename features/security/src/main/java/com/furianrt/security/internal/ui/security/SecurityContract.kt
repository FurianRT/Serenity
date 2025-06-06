package com.furianrt.security.internal.ui.security

internal sealed interface SecurityUiState {
    data object Loading : SecurityUiState
    data class Success(
        val isPinEnabled: Boolean,
        val isFingerprintEnabled: Boolean,
        val recoveryEmail: String?,
        val requestDelay: Int,
    ) : SecurityUiState
}

internal sealed interface SecurityEvent {
    data object OnButtonBackClick : SecurityEvent
    data class OnEnablePinCheckChanged(val isChecked: Boolean) : SecurityEvent
    data object OnChangeEmailClick : SecurityEvent
    data class OnFingerprintCheckChanged(val isChecked: Boolean) : SecurityEvent
    data object OnPinDelayClick : SecurityEvent
    data class OnPinDelaySelected(val delay: Int) : SecurityEvent
}

internal sealed interface SecurityEffect {
    data object CloseScreen : SecurityEffect
    data object OpenChangePinScreen : SecurityEffect
    data object OpenChangeEmailScreen : SecurityEffect
    data object ShowPinDelaysDialog : SecurityEffect
}