package com.furianrt.backup.internal.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.furianrt.backup.R
import com.furianrt.uikit.R as uiR
import com.furianrt.backup.internal.ui.BackupUiState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun BackupDate(
    date: BackupUiState.Content.Success.SyncDate,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(
        R.string.backup_last_sync_time_title,
        when (date) {
            is BackupUiState.Content.Success.SyncDate.None -> {
                stringResource(R.string.backup_last_sync_time_none_title)
            }

            is BackupUiState.Content.Success.SyncDate.Today -> {
                stringResource(uiR.string.today_title)
            }

            is BackupUiState.Content.Success.SyncDate.Yesterday -> {
                stringResource(uiR.string.yesterday_title)
            }

            is BackupUiState.Content.Success.SyncDate.Other -> date.text
        },
    )
    Crossfade(
        modifier = modifier,
        targetState = title,
    ) { targetState ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.5f),
            text = targetState,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        BackupDate(date = BackupUiState.Content.Success.SyncDate.Yesterday)
    }
}
