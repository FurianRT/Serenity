package com.furianrt.security.internal.ui.lock.check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.security.R
import com.furianrt.security.internal.domain.CheckPinUseCase
import com.furianrt.security.internal.domain.GetEmailSendTimerUseCase
import com.furianrt.security.internal.domain.GetPartiallyHiddenEmailUseCase
import com.furianrt.security.internal.domain.SendPinRecoveryEmailUseCase
import com.furianrt.security.internal.domain.repositories.SecurityRepository
import com.furianrt.security.internal.ui.lock.entities.PinConstants
import com.furianrt.security.internal.ui.lock.entities.PinCount
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.extensions.toTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val PIN_CHECK_DELAY = 100L

@HiltViewModel
internal class CheckPinViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val resourcesManager: ResourcesManager,
    private val checkPinUseCase: CheckPinUseCase,
    private val sendPinRecoveryEmailUseCase: SendPinRecoveryEmailUseCase,
    private val getPartiallyHiddenEmailUseCase: GetPartiallyHiddenEmailUseCase,
    getEmailSendTimerUseCase: GetEmailSendTimerUseCase,
) : ViewModel() {

    private var currentPin = MutableStateFlow("")
    private val showForgotPinButtonLoading = MutableStateFlow(false)

    val state: StateFlow<CheckPinUiState> = combine(
        securityRepository.isFingerprintEnabled(),
        currentPin,
        getEmailSendTimerUseCase(),
        showForgotPinButtonLoading,
    ) { isFingerprintEnabled, pin, emailSendTimer, isForgotPinButtonLoading ->
        buildState(
            isFingerprintEnabled = isFingerprintEnabled,
            pin = pin,
            emailSendTimer = emailSendTimer,
            isForgotButtonLoading = isForgotPinButtonLoading,
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

            is CheckPinEvent.OnForgotPinClick -> launch {
                _effect.tryEmit(
                    CheckPinEffect.ShowForgotPinDialog(
                        getPartiallyHiddenEmailUseCase().first(),
                    )
                )
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
            currentPin.value.length == PinConstants.MAX_PIN_LENGTH - 1 -> {
                updateCurrentPin(key)
                checkPinJob = launch {
                    delay(PIN_CHECK_DELAY)
                    if (checkPinUseCase(currentPin.value)) {
                        _effect.tryEmit(CheckPinEffect.ShowPinSuccess)
                    } else {
                        _effect.tryEmit(CheckPinEffect.ShowWrongPinError)
                    }
                    delay(PIN_CHECK_DELAY)
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
            _effect.emit(CheckPinEffect.ShowBiometricScanner)
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
        showForgotPinButtonLoading.update { true }
        launch {
            sendPinRecoveryEmailUseCase(
                subject = resourcesManager.getString(R.string.pin_recovery_email_subject),
                text = resourcesManager.getString(
                    R.string.pin_recovery_email_content,
                    securityRepository.getPin().first()!!,
                ),
            ).onSuccess {
                _effect.tryEmit(CheckPinEffect.ShowSendEmailSuccess)
                securityRepository.setLastEmailSendTime(System.currentTimeMillis())
                showForgotPinButtonLoading.update { false }
            }.onFailure {
                _effect.tryEmit(CheckPinEffect.ShowSendEmailFailure)
                showForgotPinButtonLoading.update { false }
            }
        }
    }

    private fun buildState(
        isFingerprintEnabled: Boolean,
        pin: String,
        emailSendTimer: Long,
        isForgotButtonLoading: Boolean,
    ) = CheckPinUiState(
        showFingerprint = isFingerprintEnabled && securityRepository.isBiometricAvailable(),
        pins = PinCount.fromPin(pin),
        forgotPinButtonState = when {
            emailSendTimer > 0L -> {
                CheckPinUiState.ForgotPinButtonState.Timer(emailSendTimer.toTimeString())
            }

            isForgotButtonLoading -> CheckPinUiState.ForgotPinButtonState.Loading
            else -> CheckPinUiState.ForgotPinButtonState.Enabled
        },
    )
}