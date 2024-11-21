package com.furianrt.lock.internal.ui.check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.SecurityRepository
import com.furianrt.lock.R
import com.furianrt.lock.internal.domain.CheckPinUseCase
import com.furianrt.lock.internal.domain.SendPinRecoveryEmailUseCase
import com.furianrt.lock.internal.ui.entities.Constants
import com.furianrt.lock.internal.ui.entities.PinCount
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.extensions.toTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val SEND_EMAIL_INTERVAL = 1000 * 60 * 5 // 5 min

@HiltViewModel
internal class CheckPinViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val resourcesManager: ResourcesManager,
    private val checkPinUseCase: CheckPinUseCase,
    private val sendPinRecoveryEmailUseCase: SendPinRecoveryEmailUseCase,
) : ViewModel() {

    private var currentPin = MutableStateFlow("")

    private val forgotPinButtonState = MutableStateFlow<CheckPinUiState.ForgotPinButtonState>(
        CheckPinUiState.ForgotPinButtonState.Enabled,
    )

    val state = combine(
        securityRepository.isFingerprintEnabled(),
        currentPin,
        forgotPinButtonState,
    ) { isFingerprintEnabled, pin, forgotButtonState ->
        CheckPinUiState(
            showFingerprint = isFingerprintEnabled && securityRepository.isBiometricAvailable(),
            pins = PinCount.fromPin(pin),
            forgotPinButtonState = forgotButtonState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CheckPinUiState(),
    )

    private val _effect = MutableSharedFlow<CheckPinEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var checkPinJob: Job? = null
    private var scannerJob: Job? = null

    fun onEvent(event: CheckPinEvent) {
        when (event) {
            is CheckPinEvent.OnKeyEntered -> onKeyEntered(event.key)
            is CheckPinEvent.OnClearKeyClick -> onClearKeyClick()
            is CheckPinEvent.OnCloseClick -> closeScreen()
            is CheckPinEvent.OnSendRecoveryEmailClick -> sendRecoveryEmail()
            is CheckPinEvent.OnScreenStarted, is CheckPinEvent.OnFingerprintClick -> {
                if (scannerJob?.isCompleted != false) {
                    scannerJob = tryShowBiometricScanner()
                }
            }

            is CheckPinEvent.OnForgotPinClick -> {
                _effect.tryEmit(CheckPinEffect.ShowForgotPinDialog)
            }

            is CheckPinEvent.OnBiometricSucceeded -> {
                _effect.tryEmit(CheckPinEffect.ShowPinSuccess)
                resetPin()
            }
        }
    }

    private fun onKeyEntered(key: Int) {
        when {
            checkPinJob?.isCompleted == false -> return
            currentPin.value.length == Constants.MAX_PIN_LENGTH - 1 -> {
                updateCurrentPin(key)
                checkPinJob = launch {
                    delay(100)
                    if (checkPinUseCase(currentPin.value)) {
                        _effect.tryEmit(CheckPinEffect.ShowPinSuccess)
                    } else {
                        _effect.tryEmit(CheckPinEffect.ShowWrongPinError)
                    }
                    delay(100)
                    resetPin()
                }
            }

            else -> updateCurrentPin(key)
        }
    }

    private fun tryShowBiometricScanner() = launch {
        val isFingerprintEnabled = securityRepository.isBiometricAvailable() &&
                securityRepository.isFingerprintEnabled().first()
        if (isFingerprintEnabled) {
            delay(100)
            _effect.tryEmit(CheckPinEffect.ShowBiometricScanner)
        }
    }

    private fun updateCurrentPin(newKey: Int) {
        currentPin.update { it + newKey }
    }

    private fun onClearKeyClick() {
        currentPin.update { it.dropLast(1) }
    }

    private fun resetPin() {
        currentPin.update { "" }
    }

    private fun closeScreen() {
        resetPin()
        _effect.tryEmit(CheckPinEffect.CloseScreen)
    }

    private fun sendRecoveryEmail() {
        forgotPinButtonState.update { CheckPinUiState.ForgotPinButtonState.Disabled }
        launch {
            sendPinRecoveryEmailUseCase(
                subject = resourcesManager.getString(R.string.pin_recovery_email_subject),
                text = resourcesManager.getString(
                    R.string.pin_recovery_email_content,
                    securityRepository.getPin().first()!!,
                ),
            ).onSuccess {
                _effect.tryEmit(CheckPinEffect.ShowSendEmailSuccess)
                startEmailSendTimer()
            }.onFailure {
                _effect.tryEmit(CheckPinEffect.ShowSendEmailFailure)
                forgotPinButtonState.update { CheckPinUiState.ForgotPinButtonState.Enabled }
            }
        }
    }

    private suspend fun startEmailSendTimer() {
        var interval = SEND_EMAIL_INTERVAL
        while (interval > 0) {
            delay(1000)
            interval -= 1000
            forgotPinButtonState.update {
                CheckPinUiState.ForgotPinButtonState.Timer(interval.toTimeString())
            }
        }
        forgotPinButtonState.update { CheckPinUiState.ForgotPinButtonState.Enabled }
    }
}