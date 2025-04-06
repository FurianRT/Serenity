package com.furianrt.backup.internal.ui

import android.content.Intent
import android.content.IntentSender
import com.furianrt.backup.internal.domain.exceptions.AuthException
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

        sealed class AuthState(open val isLoading: Boolean) {
            data class SignedIn(
                override val isLoading: Boolean,
                val email: String,
            ) : AuthState(isLoading)

            data class SignedOut(
                override val isLoading: Boolean,
            ) : AuthState(isLoading)
        }
    }

    data object Loading : BackupUiState
}

internal sealed interface BackupScreenEvent {
    data object OnButtonBackClick : BackupScreenEvent
    data object OnButtonBackupClick : BackupScreenEvent
    data object OnButtonRestoreClick : BackupScreenEvent
    data object OnBackupPeriodClick : BackupScreenEvent
    data class OnAutoBackupCheckChange(val isChecked: Boolean) : BackupScreenEvent
    data class OnQuestionClick(val question: Question) : BackupScreenEvent
    data object OnSignInClick : BackupScreenEvent
    data object OnSignOutClick : BackupScreenEvent
    data object OnSignOutConfirmClick : BackupScreenEvent
    data class OnBackupResolutionComplete(val intent: Intent?) : BackupScreenEvent
    data class OnBackupResolutionFailure(val error: AuthException) : BackupScreenEvent
}

internal sealed interface BackupEffect {
    data object CloseScreen : BackupEffect
    data object ShowConfirmSignOutDialog : BackupEffect
    data class ShowBackupResolution(val intentSender: IntentSender) : BackupEffect
    data class ShowErrorToast(val text: String) : BackupEffect
}
