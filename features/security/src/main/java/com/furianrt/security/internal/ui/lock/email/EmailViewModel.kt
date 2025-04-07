package com.furianrt.security.internal.ui.lock.email

import androidx.compose.foundation.text.input.delete
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.security.api.ChangeEmailRoute
import com.furianrt.security.internal.domain.LockManager
import com.furianrt.security.internal.domain.ValidateEmailUseCase
import com.furianrt.security.internal.domain.repositories.SecurityRepository
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class EmailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val securityRepository: SecurityRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val lockManager: LockManager,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ChangeEmailRoute>()

    private val _state = MutableStateFlow(EmailUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EmailEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private var validateEmailJob: Job? = null

    init {
        launch {
            state.value.email.edit {
                delete(0, originalText.length)
                append(securityRepository.getPinRecoveryEmail().first())
            }
        }
    }

    fun onEvent(event: EmailEvent) {
        when (event) {
            is EmailEvent.OnConfirmClick -> validateEmail(state.value.email.text.toString())
            is EmailEvent.OnCloseClick -> _effect.tryEmit(EmailEffect.CloseScreen)
        }
    }

    private fun validateEmail(email: String) {
        if (validateEmailJob?.isCompleted == false) {
            return
        }
        _state.update { it.copy(isLoading = true) }
        if (validateEmailUseCase(email)) {
            lockManager.authorize()
            validateEmailJob = launch {
                savePinAndEmail(route.pin, email)
                _effect.tryEmit(EmailEffect.CloseScreen)
            }
        } else {
            _state.update { it.copy(isLoading = false) }
            _effect.tryEmit(EmailEffect.ShowEmailFormatError)
        }
    }

    private suspend fun savePinAndEmail(pin: String?, email: String) {
        securityRepository.setPinRecoveryEmail(email)
        if (pin != null) {
            securityRepository.setPin(pin)
        }
    }
}