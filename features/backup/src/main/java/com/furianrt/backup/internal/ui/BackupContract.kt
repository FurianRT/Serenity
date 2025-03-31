package com.furianrt.backup.internal.ui

import com.furianrt.backup.internal.ui.entities.Question
import kotlinx.collections.immutable.ImmutableList

internal sealed interface BackupUiState {

    data class Success(
        val isAutoBackupEnabled: Boolean,
        val backupPeriod: String,
        val lastSyncDateTime: String?,
        val questions: ImmutableList<Question>,
        val authState: AuthState,
    ) : BackupUiState {

        val isSignedIn = authState is AuthState.SignedIn

        sealed interface AuthState {
            data class SignedIn(
                val email: String,
            ) : AuthState

            data object SignedOut : AuthState
        }
    }

    data object Loading : BackupUiState
}

internal sealed interface BackupScreenEvent {
    data object OnButtonBackClick : BackupScreenEvent
    data object OnBackupPeriodClick : BackupScreenEvent
    data class OnAutoBackupCheckChange(val isChecked: Boolean) : BackupScreenEvent
    data class OnQuestionClick(val question: Question) : BackupScreenEvent
    data object OnSignInClick : BackupScreenEvent
    data object OnSignOunClick : BackupScreenEvent
}

internal sealed interface BackupEffect {
    data object CloseScreen : BackupEffect
}
