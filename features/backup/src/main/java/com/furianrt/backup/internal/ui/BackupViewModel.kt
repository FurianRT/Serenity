package com.furianrt.backup.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.backup.internal.domain.GetPopularQuestionsUseCase
import com.furianrt.backup.internal.domain.entities.PopularQuestion
import com.furianrt.backup.internal.extensions.toQuestion
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.core.mapImmutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class BackupViewModel @Inject constructor(
    getPopularQuestionsUseCase: GetPopularQuestionsUseCase,
) : ViewModel() {

    private val expandedQuestionsState = MutableStateFlow(emptySet<String>())

    val state = combine(
        getPopularQuestionsUseCase(),
        expandedQuestionsState,
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
            is BackupScreenEvent.OnAutoBackupCheckChange -> {}
            is BackupScreenEvent.OnBackupPeriodClick -> {}
            is BackupScreenEvent.OnButtonBackClick -> _effect.tryEmit(BackupEffect.CloseScreen)
            is BackupScreenEvent.OnQuestionClick -> toggleQuestionExpandedState(event.question)
            is BackupScreenEvent.OnSignInClick -> {}
            is BackupScreenEvent.OnSignOunClick -> {}
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

    private fun buildState(
        questions: List<PopularQuestion>,
        expandedQuestions: Set<String>,
    ): BackupUiState = BackupUiState.Success(
        isAutoBackupEnabled = true,
        backupPeriod = "1 day",
        lastSyncDateTime = null,
        questions = questions.mapImmutable { popularQuestion ->
            popularQuestion.toQuestion(
                isExpanded = expandedQuestions.contains(popularQuestion.id),
            )
        },
        authState = BackupUiState.Success.AuthState.SignedIn(
            email = "Downinflames8@gmail.com",
        ),
        //authState = BackupUiState.Success.AuthState.SignedOut,
    )
}