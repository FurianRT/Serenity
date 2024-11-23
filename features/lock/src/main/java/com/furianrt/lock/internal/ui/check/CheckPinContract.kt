package com.furianrt.lock.internal.ui.check

import com.furianrt.lock.internal.ui.entities.PinCount

internal data class CheckPinUiState(
    val showFingerprint: Boolean = false,
    val pins: PinCount = PinCount.ZERO,
    val forgotPinButtonState: ForgotPinButtonState = ForgotPinButtonState.Enabled,
) {
    sealed interface ForgotPinButtonState {
        data object Enabled : ForgotPinButtonState
        data object Loading : ForgotPinButtonState
        data class Timer(val timer: String) : ForgotPinButtonState
    }
}

internal sealed interface CheckPinEvent {
    data class OnKeyEntered(val key: Int) : CheckPinEvent
    data object OnClearKeyClick : CheckPinEvent
    data object OnFingerprintClick : CheckPinEvent
    data object OnForgotPinClick : CheckPinEvent
    data object OnScreenStarted : CheckPinEvent
    data object OnBiometricSucceeded : CheckPinEvent
    data object OnSendRecoveryEmailClick : CheckPinEvent
    data object OnCloseClick : CheckPinEvent
}

internal sealed interface CheckPinEffect {
    data class ShowForgotPinDialog(val email: String?) : CheckPinEffect
    data object ShowWrongPinError : CheckPinEffect
    data object ShowPinSuccess : CheckPinEffect
    data object ShowBiometricScanner : CheckPinEffect
    data object ShowSendEmailFailure : CheckPinEffect
    data object ShowSendEmailSuccess : CheckPinEffect
    data object CloseScreen : CheckPinEffect
}