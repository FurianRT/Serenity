package com.furianrt.backup.internal.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.furianrt.backup.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.ConfirmationDialogButton
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
        cancelButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_cancel),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onDismissRequest,
            )
        },
        confirmButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_backup),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onConfirmClick()
                    onDismissRequest()
                },
            )
        },
        title = AnnotatedString(stringResource(R.string.backup_warning_title)),
        hint = AnnotatedString(stringResource(R.string.backup_warning_body)),
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
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
