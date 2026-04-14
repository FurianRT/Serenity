package com.furianrt.notelist.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.notelist.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import com.furianrt.uikit.R as uiR

@Composable
internal fun BackupFailureMessage(
    hazeState: HazeState,
    onCloseClick: () -> Unit,
    onFixClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    noiseFactor = 0f,
                    blurRadius = 12.dp,
                )
            )
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp),
            text = stringResource(R.string.note_list_backup_failure_message),
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier
                .padding(end = 12.dp)
                .align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onCloseClick,
                contentPadding = PaddingValues(8.dp),
            ) {
                Text(
                    text = stringResource(uiR.string.action_close),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            TextButton(
                onClick = onFixClick,
                contentPadding = PaddingValues(8.dp),
            ) {
                Text(
                    text = stringResource(uiR.string.action_fix),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        BackupFailureMessage(
            hazeState = rememberHazeState(),
            onFixClick = {},
            onCloseClick = {}
        )
    }
}
