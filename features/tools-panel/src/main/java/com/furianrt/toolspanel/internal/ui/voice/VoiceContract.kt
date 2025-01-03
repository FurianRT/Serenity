package com.furianrt.toolspanel.internal.ui.voice

import com.furianrt.toolspanel.api.VoiceRecord

internal data class VoiceUiState(
    val isPaused: Boolean,
    val duration: String,
)

internal sealed interface VoiceEvent {
    data object OnPauseClick : VoiceEvent
    data object OnCancelClick : VoiceEvent
    data object OnDoneClick : VoiceEvent
    data object OnEnterComposition : VoiceEvent
    data object OnScreenStopped : VoiceEvent
}

internal sealed interface VoiceEffect {
    data class SendRecordCompleteEvent(val record: VoiceRecord) : VoiceEffect
    data object CloseRecording : VoiceEffect
}
