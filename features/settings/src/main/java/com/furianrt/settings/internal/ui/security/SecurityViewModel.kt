package com.furianrt.settings.internal.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.doWithState
import com.furianrt.domain.repositories.SecurityRepository
import com.furianrt.settings.internal.ui.security.SecurityEvent.*
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class SecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
) : ViewModel() {

    val state = combine(
        securityRepository.getPin(),
        securityRepository.getPinRecoveryEmail(),
        securityRepository.getPinRequestDelay(),
        securityRepository.isFingerprintEnabled()
    ) { pin, email, delay, fingerprint ->
        buildState(pin, delay, email, fingerprint)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SecurityUiState.Loading,
    )

    private val _effect = MutableSharedFlow<SecurityEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var fingerprintJob: Job? = null

    fun onEvent(event: SecurityEvent) {
        when (event) {
            is OnButtonBackClick -> _effect.tryEmit(SecurityEffect.CloseScreen)
            is OnChangeEmailClick -> _effect.tryEmit(SecurityEffect.OpenChangeEmailScreen)
            is OnEnablePinCheckChanged -> state.doWithState<SecurityUiState.Success> { success ->
                if (success.isPinEnabled) {
                    clearPin()
                } else {
                    openChangePinScreen()
                }
            }

            is OnFingerprintCheckChanged -> {
                if (fingerprintJob?.isCompleted != false) {
                    fingerprintJob = toggleFingerprint()
                }

            }

            is OnPinDelayClick -> _effect.tryEmit(SecurityEffect.ShowPinDelaysDialog)
            is OnPinDelaySelected -> launch {
                securityRepository.setPinRequestDelay(event.delay)
            }
        }
    }

    private fun toggleFingerprint() = launch {
        state.doWithState<SecurityUiState.Success> { success ->
            securityRepository.setFingerprintEnabled(!success.isFingerprintEnabled)
        }
    }

    private fun clearPin() {
        launch { securityRepository.setPin(null) }
    }

    private fun openChangePinScreen() {
        _effect.tryEmit(SecurityEffect.OpenChangePinScreen)
    }

    private fun buildState(
        pin: String?,
        requestDelay: Int,
        recoveryEmail: String?,
        isFingerprintEnabled: Boolean,
    ) = SecurityUiState.Success(
        isPinEnabled = pin != null,
        isFingerprintEnabled = isFingerprintEnabled,
        recoveryEmail = recoveryEmail,
        requestDelay = requestDelay,
    )
}