package com.furianrt.security.internal.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.security.internal.domain.repositories.SecurityRepository
import com.furianrt.security.internal.ui.security.SecurityEvent.*
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class SecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
) : ViewModel() {

    val state: StateFlow<SecurityUiState> = combine(
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
            is OnEnablePinCheckChanged -> if (event.isChecked) {
                openChangePinScreen()
            } else {
                clearPin()
            }

            is OnFingerprintCheckChanged -> {
                if (fingerprintJob?.isCompleted != false) {
                    fingerprintJob = enableFingerprint(event.isChecked)
                }

            }

            is OnPinDelayClick -> _effect.tryEmit(SecurityEffect.ShowPinDelaysDialog)
            is OnPinDelaySelected -> launch {
                securityRepository.setPinRequestDelay(event.delay)
            }
        }
    }

    private fun enableFingerprint(isEnabled: Boolean) = launch {
        securityRepository.setFingerprintEnabled(isEnabled)
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