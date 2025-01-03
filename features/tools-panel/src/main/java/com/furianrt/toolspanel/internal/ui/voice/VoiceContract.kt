package com.furianrt.toolspanel.internal.ui.voice

import androidx.annotation.FloatRange
import com.furianrt.toolspanel.api.VoiceRecord

internal data class VoiceUiState(
    val isPaused: Boolean,
    val duration: String,
    @FloatRange(from = 0.0, to = 1.0) val volume: Float,
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
