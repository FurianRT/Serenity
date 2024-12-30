package com.furianrt.toolspanel.internal.ui.voice

internal data class VoiceUiState(
    val isRecording: Boolean = true,
    val duration: Int = 0,
)

internal sealed interface VoiceEvent {
    data object OnRecordClick : VoiceEvent
    data object OnCancelClick : VoiceEvent
    data object OnDoneClick : VoiceEvent
}

internal sealed interface VoiceEffect {
    data object SendRecordCompleteEvent : VoiceEffect
}
