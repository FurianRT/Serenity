package com.furianrt.backup.internal.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.backup.R
import com.furianrt.backup.internal.ui.BackupUiState
import com.furianrt.backup.internal.ui.BackupUiState.Content.Success.SyncProgress
import com.furianrt.uikit.anim.ShakingState
import com.furianrt.uikit.anim.rememberShakingState
import com.furianrt.uikit.anim.shakable
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun BottomPanel(
    syncProgress: SyncProgress,
    lastSyncDate: BackupUiState.Content.Success.SyncDate,
    shakingState: ShakingState,
    onBackupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.tertiary,
        )
        SyncButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .shakable(shakingState),
            text = when (syncProgress) {
                is SyncProgress.BackupStarting -> {
                    stringResource(R.string.backup_starting_message)
                }

                is SyncProgress.RestoreStarting -> {
                    stringResource(R.string.restore_starting_message)
                }

                is SyncProgress.BackupProgress -> stringResource(
                    R.string.backup_progress_message,
                    syncProgress.syncedNotesCount,
                    syncProgress.totalNotesCount,
                )

                is SyncProgress.RestoreProgress -> stringResource(
                    R.string.restore_progress_message,
                    syncProgress.syncedNotesCount,
                    syncProgress.totalNotesCount,
                )

                else -> stringResource(R.string.backup_backup_data_title)
            },
            progress = when (syncProgress) {
                is SyncProgress.BackupStarting, SyncProgress.RestoreStarting -> 0f
                is SyncProgress.BackupProgress -> {
                    syncProgress.syncedNotesCount / syncProgress.totalNotesCount.toFloat()
                }

                is SyncProgress.RestoreProgress -> {
                    syncProgress.syncedNotesCount / syncProgress.totalNotesCount.toFloat()
                }

                else -> null
            },
            isEnabled = true,
            onClick = onBackupClick,
        )
        BackupDate(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            date = lastSyncDate,
        )
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        BottomPanel(
            syncProgress = SyncProgress.Idle,
            lastSyncDate = BackupUiState.Content.Success.SyncDate.Yesterday,
            shakingState = rememberShakingState(),
            onBackupClick = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewProgress() {
    SerenityTheme {
        BottomPanel(
            syncProgress = SyncProgress.BackupProgress(
                syncedNotesCount = 12,
                totalNotesCount = 25,
            ),
            lastSyncDate = BackupUiState.Content.Success.SyncDate.Yesterday,
            shakingState = rememberShakingState(),
            onBackupClick = {},
        )
    }
}
