package com.furianrt.lock.internal.ui.change

import com.furianrt.lock.internal.ui.entities.PinCount

internal data class ChangePinUiState(
    val pins: PinCount = PinCount.ZERO,
    val mode: Mode = Mode.INITIAL,
) {
    enum class Mode {
        INITIAL, REPEAT
    }
}

internal sealed interface ChangePinEvent {
    data class OnKeyEntered(val key: Int) : ChangePinEvent
    data object OnClearKeyClick : ChangePinEvent
    data object OnCloseClick : ChangePinEvent
}

internal sealed interface ChangePinEffect {
    data class OpenEmailScreen(val pin: String) : ChangePinEffect
    data object ShowPinDoesNotMatchError : ChangePinEffect
    data object CloseScreen : ChangePinEffect
}