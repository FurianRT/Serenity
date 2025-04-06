package com.furianrt.backup.internal.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.backup.internal.domain.AuthorizeUseCase
import com.furianrt.backup.internal.domain.GetBackupProfileUseCase
import com.furianrt.backup.internal.domain.GetPopularQuestionsUseCase
import com.furianrt.backup.internal.domain.SignInUseCase
import com.furianrt.backup.internal.domain.SignOutUseCase
import com.furianrt.backup.internal.domain.entities.AuthResult
import com.furianrt.backup.internal.domain.entities.PopularQuestion
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.backup.internal.extensions.toQuestion
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.core.doWithState
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.BackupProfile
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.furianrt.uikit.R as uiR

@HiltViewModel
internal class BackupViewModel @Inject constructor(
    getPopularQuestionsUseCase: GetPopularQuestionsUseCase,
    getBackupProfileUseCase: GetBackupProfileUseCase,
    private val backupRepository: BackupRepository,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val authorizeUseCase: AuthorizeUseCase,
    private val resourcesManager: ResourcesManager,
) : ViewModel() {

    private val expandedQuestionsState = MutableStateFlow(emptySet<String>())
    private val isAuthInProgressState = MutableStateFlow(false)

    val state = combine(
        getPopularQuestionsUseCase(),
        expandedQuestionsState,
        getBackupProfileUseCase(),
        isAuthInProgressState,
        backupRepository.isAutoBackupEnabled(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BackupUiState.Loading,
    )

    private val _effect = MutableSharedFlow<BackupEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: BackupScreenEvent) {
        when (event) {
            is BackupScreenEvent.OnAutoBackupCheckChange -> launch {
                backupRepository.setAutoBackupEnabled(event.isChecked)
            }

            is BackupScreenEvent.OnButtonBackupClick -> {}
            is BackupScreenEvent.OnButtonRestoreClick -> {}
            is BackupScreenEvent.OnBackupPeriodClick -> {}
            is BackupScreenEvent.OnButtonBackClick -> _effect.tryEmit(BackupEffect.CloseScreen)
            is BackupScreenEvent.OnQuestionClick -> toggleQuestionExpandedState(event.question)
            is BackupScreenEvent.OnSignInClick -> authorize()
            is BackupScreenEvent.OnSignOutClick -> {
                _effect.tryEmit(BackupEffect.ShowConfirmSignOutDialog)
            }

            is BackupScreenEvent.OnSignOutConfirmClick -> signOut()
            is BackupScreenEvent.OnBackupResolutionComplete -> launch {
                signIn(intent = event.intent)
            }

            is BackupScreenEvent.OnBackupResolutionFailure -> showError(event.error)
        }
    }

    private fun toggleQuestionExpandedState(question: Question) {
        expandedQuestionsState.update { expandedQuestions ->
            expandedQuestions.toMutableSet().apply {
                if (expandedQuestions.contains(question.id)) {
                    remove(question.id)
                } else {
                    add(question.id)
                }
            }
        }
    }

    private fun authorize() = launch {
        when (val result = authorizeUseCase()) {
            is AuthResult.Success -> signIn(accessToken = result.accessToken)
            is AuthResult.Failure -> result.error.printStackTrace()
            is AuthResult.Resolution -> {
                _effect.tryEmit(BackupEffect.ShowBackupResolution(result.intentSender))
            }
        }
    }

    private suspend fun signIn(
        accessToken: String? = null,
        intent: Intent? = null,
    ) {
        isAuthInProgressState.update { true }
        val result = if (intent != null) {
            signInUseCase(intent)
        } else {
            signInUseCase(accessToken)
        }
        result.onFailure(::showError)
        isAuthInProgressState.update { false }
    }

    private fun signOut() {
        state.doWithState<BackupUiState.Success> { successState ->
            if (successState.authState is BackupUiState.Success.AuthState.SignedIn) {
                isAuthInProgressState.update { true }
                launch {
                    signOutUseCase(successState.authState.email).onFailure(::showError)
                    isAuthInProgressState.update { false }
                }
            }
        }
    }

    private fun showError(error: Throwable) {
        error.printStackTrace()
        val message = if (error is AuthException) {
            when (error) {
                is AuthException.NetworkException -> {
                    resourcesManager.getString(uiR.string.network_error)
                }

                else -> resourcesManager.getString(
                    uiR.string.general_error_with_code,
                    error.code,
                )
            }
        } else {
            resourcesManager.getString(
                uiR.string.general_error_with_code,
                AuthException.UnknownErrorException.CODE,
            )
        }

        _effect.tryEmit(BackupEffect.ShowErrorToast(text = message))
    }

    private fun buildState(
        questions: List<PopularQuestion>,
        expandedQuestions: Set<String>,
        backupProfile: BackupProfile?,
        isAuthInProgress: Boolean,
        isAutoBackupEnabled: Boolean,
    ): BackupUiState = BackupUiState.Success(
        isAutoBackupEnabled = isAutoBackupEnabled,
        backupPeriod = "1 day",
        lastSyncDateTime = null,
        questions = questions.mapImmutable { popularQuestion ->
            popularQuestion.toQuestion(
                isExpanded = expandedQuestions.contains(popularQuestion.id),
            )
        },
        authState = if (backupProfile != null) {
            BackupUiState.Success.AuthState.SignedIn(
                email = backupProfile.email,
                isLoading = isAuthInProgress,
            )
        } else {
            BackupUiState.Success.AuthState.SignedOut(
                isLoading = isAuthInProgress,
            )
        },
    )
}