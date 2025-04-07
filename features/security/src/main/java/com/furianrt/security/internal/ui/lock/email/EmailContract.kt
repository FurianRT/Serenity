package com.furianrt.security.internal.ui.lock.email

import androidx.compose.foundation.text.input.TextFieldState

internal data class EmailUiState(
    val email: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
)

internal sealed interface EmailEvent {
    data object OnConfirmClick : EmailEvent
    data object OnCloseClick : EmailEvent
}

internal sealed interface EmailEffect {
    data object CloseScreen : EmailEffect
    data object ShowEmailFormatError : EmailEffect
}