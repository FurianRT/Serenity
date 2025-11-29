package com.furianrt.settings.internal.ui.noteSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class NoteSettingsViewModel @Inject constructor(
    private val appearanceRepository: AppearanceRepository,
) : ViewModel() {

    val state: StateFlow<NoteSettingsState> = combine(
        appearanceRepository.isAutoDetectLocationEnabled(),
        appearanceRepository.isMinimalisticHomeScreenEnabled(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteSettingsState.Loading,
    )

    private val _effect = MutableSharedFlow<NoteSettingsEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<NoteSettingsEffect> = _effect.asSharedFlow()

    fun onEvent(event: NoteSettingsEvent) {
        when (event) {
            is NoteSettingsEvent.OnButtonBackClick -> {
                _effect.tryEmit(NoteSettingsEffect.CloseScreen)
            }

            is NoteSettingsEvent.OnEnableAutoDetectLocationChanged -> launch {
                appearanceRepository.setAutoDetectLocationEnabled(event.isEnabled)
            }

            is NoteSettingsEvent.OnEnableMinimalisticHomeScreenChanged -> launch {
                appearanceRepository.setMinimalisticHomeScreenEnabled(event.isEnabled)
            }
        }
    }

    private fun buildState(
        isAutoDetectLocationEnabled: Boolean,
        isMinimalisticHomeScreenEnabled: Boolean,
    ): NoteSettingsState = NoteSettingsState.Success(
        isAutoDetectLocationEnabled = isAutoDetectLocationEnabled,
        isMinimalisticHomeScreenEnabled = isMinimalisticHomeScreenEnabled,
    )
}