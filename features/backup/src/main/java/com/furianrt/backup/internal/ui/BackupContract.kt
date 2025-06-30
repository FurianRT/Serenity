package com.furianrt.backup.internal.ui

import android.content.Intent
import android.content.IntentSender
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.ui.BackupUiState.Success.SyncProgress.*
import com.furianrt.backup.internal.ui.entities.Question
import kotlinx.collections.immutable.ImmutableList

internal sealed interface BackupUiState {

    data class Success(
        val isAutoBackupEnabled: Boolean,
        val backupPeriod: BackupPeriod,
        val lastSyncDate: SyncDate,
        val questions: ImmutableList<Question>,
        val authState: AuthState,
        val syncProgress: SyncProgress,
    ) : BackupUiState {

        val isSignedIn = authState is AuthState.SignedIn
        val isBackupInProgress = syncProgress is BackupStarting || syncProgress is BackupProgress
        val isRestoreInProgress = syncProgress is RestoreStarting || syncProgress is RestoreProgress
        val isSyncInProgress = isBackupInProgress || isRestoreInProgress
        val hasSyncError = syncProgress is Failure
        val isSyncSuccess = syncProgress is SyncProgress.Success

        sealed class AuthState(open val isLoading: Boolean) {
            data class SignedIn(
                override val isLoading: Boolean,
                val email: String,
            ) : AuthState(isLoading)

            data class SignedOut(
                override val isLoading: Boolean,
            ) : AuthState(isLoading)
        }

        sealed interface SyncDate {
            data object Today : SyncDate
            data object Yesterday : SyncDate
            data object None : SyncDate
            data class Other(val text: String) : SyncDate
        }

        sealed interface SyncProgress {
            data object Idle : SyncProgress
            data object BackupStarting : SyncProgress
            data object RestoreStarting : SyncProgress
            data object Success : SyncProgress
            data class BackupProgress(
                val syncedNotesCount: Int,
                val totalNotesCount: Int,
            ) : SyncProgress

            data class RestoreProgress(
                val syncedNotesCount: Int,
                val totalNotesCount: Int,
            ) : SyncProgress

            data class Failure(
                val backup: Boolean,
                val restore: Boolean,
            ) : SyncProgress
        }
    }

    data object Loading : BackupUiState
}

internal sealed interface BackupScreenEvent {
    data object OnButtonBackClick : BackupScreenEvent
    data object OnButtonBackupClick : BackupScreenEvent
    data object OnConfirmBackupClick : BackupScreenEvent
    data object OnButtonRestoreClick : BackupScreenEvent
    data object OnBackupPeriodClick : BackupScreenEvent
    data class OnBackupPeriodSelected(val period: BackupPeriod) : BackupScreenEvent
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
    data object ShowBackupPeriodDialog : BackupEffect
    data class ShowBackupResolution(val intentSender: IntentSender) : BackupEffect
    data class ShowErrorToast(val text: String) : BackupEffect
    data object ShowConfirmBackupDialog : BackupEffect
}
