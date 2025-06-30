package com.furianrt.backup.internal.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.backup.R
import com.furianrt.backup.internal.domain.BackupDataManager
import com.furianrt.backup.internal.domain.RestoreDataManager
import com.furianrt.backup.internal.domain.ServiceLauncher
import com.furianrt.backup.internal.domain.entities.AuthResult
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import com.furianrt.backup.internal.domain.entities.SyncState
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.backup.internal.domain.usecases.AuthorizeUseCase
import com.furianrt.backup.internal.domain.usecases.GetBackupProfileUseCase
import com.furianrt.backup.internal.domain.usecases.GetPopularQuestionsUseCase
import com.furianrt.backup.internal.domain.usecases.SignInUseCase
import com.furianrt.backup.internal.domain.usecases.SignOutUseCase
import com.furianrt.backup.internal.extensions.toQuestion
import com.furianrt.backup.internal.extensions.toSyncDate
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnAutoBackupCheckChange
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnBackupPeriodClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnBackupPeriodSelected
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnBackupResolutionComplete
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnBackupResolutionFailure
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnButtonBackClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnButtonBackupClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnButtonRestoreClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnConfirmBackupClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnQuestionClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnSignInClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnSignOutClick
import com.furianrt.backup.internal.ui.BackupScreenEvent.OnSignOutConfirmClick
import com.furianrt.backup.internal.ui.BackupUiState.Success.SyncProgress
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.common.ErrorTracker
import com.furianrt.core.doWithState
import com.furianrt.core.mapImmutable
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import javax.inject.Inject
import com.furianrt.uikit.R as uiR

@HiltViewModel
internal class BackupViewModel @Inject constructor(
    getPopularQuestionsUseCase: GetPopularQuestionsUseCase,
    getBackupProfileUseCase: GetBackupProfileUseCase,
    private val restoreDataManager: RestoreDataManager,
    private val backupDataManager: BackupDataManager,
    private val backupRepository: BackupRepository,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val authorizeUseCase: AuthorizeUseCase,
    private val resourcesManager: ResourcesManager,
    private val serviceLauncher: ServiceLauncher,
    private val errorTracker: ErrorTracker,
) : ViewModel() {

    private val expandedQuestionsState = MutableStateFlow(emptySet<String>())
    private val isAuthInProgressState = MutableStateFlow(false)
    private val questionsListFlow = combine(
        getPopularQuestionsUseCase(),
        expandedQuestionsState,
    ) { allQuestions, expandedQuestions ->
        allQuestions.mapImmutable { popularQuestion ->
            popularQuestion.toQuestion(
                isExpanded = expandedQuestions.contains(popularQuestion.id),
            )
        }
    }

    private val authStatusFlow = combine(
        getBackupProfileUseCase(),
        isAuthInProgressState,
    ) { profile, isInProgress ->
        if (profile != null) {
            BackupUiState.Success.AuthState.SignedIn(
                email = profile.email,
                isLoading = isInProgress,
            )
        } else {
            BackupUiState.Success.AuthState.SignedOut(
                isLoading = isInProgress,
            )
        }
    }

    private val syncProgressState = combine(
        backupDataManager.state,
        restoreDataManager.state,
    ) { backupSyncState, restoreSyncState ->
        when {
            backupSyncState is SyncState.Starting -> SyncProgress.BackupStarting
            backupSyncState is SyncState.Progress -> SyncProgress.BackupProgress(
                syncedNotesCount = backupSyncState.syncedNotesCount,
                totalNotesCount = backupSyncState.totalNotesCount,
            )

            restoreSyncState is SyncState.Starting -> SyncProgress.RestoreStarting
            restoreSyncState is SyncState.Progress -> SyncProgress.RestoreProgress(
                syncedNotesCount = restoreSyncState.syncedNotesCount,
                totalNotesCount = restoreSyncState.totalNotesCount,
            )

            backupSyncState is SyncState.Failure || restoreSyncState is SyncState.Failure -> {
                SyncProgress.Failure(
                    backup = backupSyncState is SyncState.Failure,
                    restore = restoreSyncState is SyncState.Failure,
                )
            }

            backupSyncState is SyncState.Success || restoreSyncState is SyncState.Success -> {
                SyncProgress.Success
            }

            else -> SyncProgress.Idle
        }
    }

    val state: StateFlow<BackupUiState> = com.furianrt.core.combine(
        questionsListFlow,
        authStatusFlow,
        backupRepository.isAutoBackupEnabled(),
        backupRepository.getAutoBackupPeriod(),
        backupRepository.getLastSyncDate(),
        syncProgressState,
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
            is OnAutoBackupCheckChange -> launch {
                backupRepository.setAutoBackupEnabled(event.isChecked)
            }

            is OnButtonBackupClick -> launch {
                if (backupRepository.isBackupConfirmed().first()) {
                    backupDataManager.clearFailureState()
                    restoreDataManager.clearFailureState()
                    serviceLauncher.launchBackupService()
                } else {
                    _effect.tryEmit(BackupEffect.ShowConfirmBackupDialog)
                }
            }

            is OnConfirmBackupClick -> launch {
                backupRepository.setBackupConfirmed(confirmed = true)
                serviceLauncher.launchBackupService()
            }

            is OnButtonRestoreClick -> {
                backupDataManager.clearFailureState()
                restoreDataManager.clearFailureState()
                serviceLauncher.launchRestoreService()
            }
            is OnBackupPeriodClick -> {
                _effect.tryEmit(BackupEffect.ShowBackupPeriodDialog)
            }

            is OnBackupPeriodSelected -> launch {
                updateBackupPeriod(event.period)
            }

            is OnButtonBackClick -> _effect.tryEmit(BackupEffect.CloseScreen)
            is OnQuestionClick -> toggleQuestionExpandedState(event.question)
            is OnSignInClick -> authorize()
            is OnSignOutClick -> state.doWithState<BackupUiState.Success> { successState ->
                if (successState.isBackupInProgress || successState.isRestoreInProgress) {
                    _effect.tryEmit(
                        BackupEffect.ShowErrorToast(
                            resourcesManager.getString(R.string.backup_sync_in_progress_message),
                        )
                    )
                } else {
                    _effect.tryEmit(BackupEffect.ShowConfirmSignOutDialog)
                }
            }

            is OnSignOutConfirmClick -> {
                backupDataManager.clearFailureState()
                restoreDataManager.clearFailureState()
                signOut()
            }
            is OnBackupResolutionComplete -> launch {
                signIn(intent = event.intent)
            }

            is OnBackupResolutionFailure -> showError(event.error)
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
            is AuthResult.Failure -> showError(result.error)
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
        errorTracker.trackNonFatalError(error)
        val message = if (error is AuthException) {
            when (error) {
                is AuthException.NetworkException -> {
                    resourcesManager.getString(uiR.string.network_error)
                }

                is AuthException.ResolutionCanceled -> {
                    resourcesManager.getString(R.string.backup_sing_in_resolution_canceled_error)
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

    private suspend fun updateBackupPeriod(period: BackupPeriod) {
        state.doWithState<BackupUiState.Success> { successState ->
            if (successState.backupPeriod != period) {
                backupRepository.setAutoBackupPeriod(period)
            }
        }
    }

    private fun buildState(
        questions: ImmutableList<Question>,
        authState: BackupUiState.Success.AuthState,
        isAutoBackupEnabled: Boolean,
        backupPeriod: BackupPeriod,
        lastSyncDate: ZonedDateTime?,
        syncProgress: SyncProgress,
    ) = BackupUiState.Success(
        isAutoBackupEnabled = isAutoBackupEnabled,
        backupPeriod = backupPeriod,
        lastSyncDate = lastSyncDate.toSyncDate(),
        questions = questions,
        authState = authState,
        syncProgress = syncProgress,
    )
}
