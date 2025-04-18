package com.furianrt.backup.internal.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import com.furianrt.backup.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun BackupDate(
    date: String?,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(
        R.string.backup_last_sync_time_title,
        date ?: stringResource(R.string.backup_last_sync_time_none_title),
    )
    Crossfade(
        modifier = modifier.alpha(0.5f),
        targetState = title,
    ) { targetState ->
        Text(
            text = targetState,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        BackupDate(date = null)
    }
}
