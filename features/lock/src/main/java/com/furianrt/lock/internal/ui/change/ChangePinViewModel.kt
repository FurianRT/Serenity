package com.furianrt.lock.internal.ui.change

import androidx.lifecycle.ViewModel
import com.furianrt.domain.managers.LockManager
import com.furianrt.domain.repositories.SecurityRepository
import com.furianrt.lock.internal.ui.change.ChangePinUiState.Mode
import com.furianrt.lock.internal.ui.entities.Constants
import com.furianrt.lock.internal.ui.entities.PinCount
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class ChangePinViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val lockManager: LockManager,
) : ViewModel() {

    private var initialPin = ""
    private var repeatedPin = ""

    private var currentPin: String
        get() = when (state.value.mode) {
            Mode.INITIAL -> initialPin
            Mode.REPEAT -> repeatedPin
        }
        set(value) = when (state.value.mode) {
            Mode.INITIAL -> initialPin = value
            Mode.REPEAT -> repeatedPin = value
        }

    private val _state = MutableStateFlow(ChangePinUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ChangePinEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var changeModeJob: Job? = null

    fun onEvent(event: ChangePinEvent) {
        when (event) {
            is ChangePinEvent.OnKeyEntered -> onKeyEntered(event.key)
            is ChangePinEvent.OnClearKeyClick -> onClearKeyClick()
            is ChangePinEvent.OnCloseClick -> _effect.tryEmit(ChangePinEffect.CloseScreen)
        }
    }

    private fun onKeyEntered(key: Int) {
        when {
            changeModeJob?.isCompleted == false -> return
            currentPin.length == Constants.MAX_PIN_LENGTH - 1 -> {
                updateCurrentPin(key)
                changeModeJob = toggleMode()
            }

            else -> updateCurrentPin(key)
        }
    }

    private fun toggleMode() = launch {
        delay(250)
        when (_state.value.mode) {
            Mode.INITIAL -> _state.update { currentState ->
                currentState.copy(
                    pins = PinCount.ZERO,
                    mode = Mode.REPEAT
                )
            }

            Mode.REPEAT -> {
                checkPins()
            }
        }
    }

    private suspend fun checkPins() {
        if (initialPin == repeatedPin) {
            val email = securityRepository.getPinRecoveryEmail().first()
            if (email.isNullOrBlank()) {
                _effect.tryEmit(ChangePinEffect.OpenEmailScreen(initialPin))
            } else {
                lockManager.authorize()
                securityRepository.setPin(initialPin)
                _effect.tryEmit(ChangePinEffect.CloseScreen)
            }
        } else {
            _effect.tryEmit(ChangePinEffect.ShowPinDoesNotMatchError)
            resetPin()
        }
    }

    private fun updateCurrentPin(newKey: Int) {
        currentPin += newKey.toString()
        _state.update { it.copy(pins = PinCount.fromPin(currentPin)) }
    }

    private fun resetPin() {
        initialPin = ""
        repeatedPin = ""
        _state.update { currentState ->
            currentState.copy(
                pins = PinCount.ZERO,
                mode = Mode.INITIAL,
            )
        }
    }

    private fun onClearKeyClick() {
        currentPin = currentPin.dropLast(1)
        _state.update { it.copy(pins = PinCount.fromPin(currentPin)) }
    }
}