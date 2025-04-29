package com.furianrt.backup.internal.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.furianrt.backup.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState

@Composable
internal fun ConfirmBackupDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        title = stringResource(R.string.backup_warning_title),
        hint = stringResource(R.string.backup_warning_body),
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
        onConfirmClick = onConfirmClick,
        confirmText = stringResource(uiR.string.action_backup),
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ConfirmBackupDialog(
            hazeState = HazeState(),
            onDismissRequest = {},
        )
    }
}
