package com.furianrt.toolspanel.internal.ui.voice

import android.net.Uri
import androidx.annotation.FloatRange
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.toolspanel.api.VoiceRecord
import com.furianrt.domain.voice.AudioRecorder
import com.furianrt.toolspanel.internal.domain.VoiceVolumeHolder
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.extensions.toTimeString
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID

private const val TIME_PATTERN = "m:ss:S"
private const val TIMER_STEP_MILLS = 100L
private const val VOLUME_STEP_MILLS = 100L

private class RecordData(val id: String, val uri: Uri)

@HiltViewModel(assistedFactory = VoiceViewModel.Factory::class)
internal class VoiceViewModel @AssistedInject constructor(
    private val audioRecorder: AudioRecorder,
    private val volumeHolder: VoiceVolumeHolder,
    private val mediaRepository: MediaRepository,
    @Assisted private val noteId: String,
) : ViewModel() {

    private val durationState = MutableStateFlow(0L)
    private val isPausedState = MutableStateFlow(false)
    private val volumeState = MutableStateFlow(0f)

    val state = combine(
        durationState,
        isPausedState,
        volumeState,
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildState(
            duration = durationState.value,
            isPaused = isPausedState.value,
            volume = volumeState.value,
        ),
    )

    private val _effect = MutableSharedFlow<VoiceEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    private var recordData: RecordData? = null
    private var recordJob: Job? = null
    private var timerJob: Job? = null
    private var volumeJob: Job? = null

    fun onEvent(event: VoiceEvent) {
        when (event) {
            is VoiceEvent.OnEnterComposition -> startRecording()
            is VoiceEvent.OnCancelClick -> cancelRecording()
            is VoiceEvent.OnPauseClick -> if (isPausedState.value) {
                resumeRecording()
            } else {
                pauseRecording()
            }

            is VoiceEvent.OnDoneClick -> completeRecording()
            is VoiceEvent.OnScreenStopped -> pauseRecording()
        }
    }

    private fun startRecording() {
        if (recordJob != null || audioRecorder.isRecording) {
            return
        }
        durationState.update { 0 }
        isPausedState.update { false }
        val recordId = UUID.randomUUID().toString()
        recordJob = launch {
            val destinationFile = mediaRepository.createVoiceDestinationFile(
                noteId = noteId,
                voiceId = recordId,
            )

            if (destinationFile == null) {
                _effect.tryEmit(VoiceEffect.CloseRecording)
                return@launch
            }

            if (audioRecorder.start(destinationFile)) {
                recordData = RecordData(id = recordId, uri = destinationFile.toUri())
                startTimer()
                startVolume()
            } else {
                launch { mediaRepository.deleteVoiceFile(noteId, recordId) }
                _effect.tryEmit(VoiceEffect.CloseRecording)
            }
        }
    }

    private fun cancelRecording() {
        if (!audioRecorder.isRecording) {
            return
        }
        audioRecorder.stop()
        val recordId = recordData!!.id
        launch { mediaRepository.deleteVoiceFile(noteId, recordId) }
        reset()
        _effect.tryEmit(VoiceEffect.CloseRecording)
    }

    private fun completeRecording() {
        if (!audioRecorder.isRecording) {
            return
        }

        val result = VoiceRecord(
            id = recordData!!.id,
            uri = recordData!!.uri,
            duration = durationState.value.toInt(),
            volume = volumeHolder.getVolume(),
        )

        if (audioRecorder.stop()) {
            _effect.tryEmit(VoiceEffect.SendRecordCompleteEvent(result))
        } else {
            launch { mediaRepository.deleteVoiceFile(noteId, result.id) }
            _effect.tryEmit(VoiceEffect.CloseRecording)
        }
        reset()
    }

    private fun pauseRecording() {
        if (audioRecorder.isRecording) {
            audioRecorder.pause()
            stopTimer()
            isPausedState.update { true }
            stopVolume()
        }
    }

    private fun resumeRecording() {
        if (audioRecorder.isRecording) {
            audioRecorder.resume()
            startTimer()
            isPausedState.update { false }
            startVolume()
        }
    }

    private fun startTimer() {
        if (timerJob == null) {
            timerJob = launch {
                while (true) {
                    delay(TIMER_STEP_MILLS)
                    durationState.update { it + TIMER_STEP_MILLS }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startVolume() {
        if (volumeJob == null) {
            volumeJob = launch {
                while (true) {
                    delay(VOLUME_STEP_MILLS)
                    val volume = audioRecorder.getCurrentVolume()
                    volumeState.update { volume }
                    volumeHolder.add(volume)
                }
            }
        }
    }

    private fun stopVolume() {
        volumeJob?.cancel()
        volumeJob = null
        volumeState.update { 0f }
    }

    private fun reset() {
        recordJob?.cancel()
        recordJob = null
        stopTimer()
        stopVolume()
        recordData = null
        volumeHolder.clear()
    }

    private fun buildState(
        duration: Long,
        isPaused: Boolean,
        @FloatRange(from = 0.0, to = 1.0) volume: Float,
    ) = VoiceUiState(
        isPaused = isPaused,
        duration = duration.toTimeString(TIME_PATTERN),
        volume = volume,
    )

    @AssistedFactory
    interface Factory {
        fun create(
            noteId: String,
        ): VoiceViewModel
    }
}