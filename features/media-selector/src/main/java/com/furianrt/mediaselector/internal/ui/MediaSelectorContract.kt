package com.furianrt.mediaselector.internal.ui


internal sealed interface MediaSelectorUiState {
    data object Loading : MediaSelectorUiState
    data object Empty : MediaSelectorUiState
    data object Success : MediaSelectorUiState
}

internal sealed interface MediaSelectorEvent

internal sealed interface MediaSelectorEffect