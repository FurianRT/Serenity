package com.furianrt.toolspanel.internal.ui.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class VoiceViewModel @Inject constructor(

) : ViewModel() {

    private val durationState = MutableStateFlow(0)
    private val isRecordingState = MutableStateFlow(true)

    val state = combine(
        durationState,
        isRecordingState,
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VoiceUiState(),
    )

    private val _effect = MutableSharedFlow<VoiceEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: VoiceEvent) {
        when (event) {
            is VoiceEvent.OnRecordClick -> {
                isRecordingState.update { !it }
            }
            is VoiceEvent.OnCancelClick -> {}
            is VoiceEvent.OnDoneClick -> {
                _effect.tryEmit(VoiceEffect.SendRecordCompleteEvent)
            }
        }
    }

    private fun buildState(
        duration: Int,
        isRecording: Boolean,
    ) = VoiceUiState(
        isRecording = isRecording,
        duration = duration,
    )
}