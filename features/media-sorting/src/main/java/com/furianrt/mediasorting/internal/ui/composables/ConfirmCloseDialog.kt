package com.furianrt.mediasorting.internal.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.furianrt.mediasorting.R
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.ConfirmationDialogButton
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR
import dev.chrisbanes.haze.HazeState

@Composable
internal fun ConfirmCloseDialog(
    hazeState: HazeState,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationDialog(
        modifier = modifier,
        hint = AnnotatedString(stringResource(R.string.media_sorting_save_changes_hint)),
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
        cancelButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_discard),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    onDiscardClick()
                    onDismissRequest()
                },
            )
        },
        confirmButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_save),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onSaveClick()
                    onDismissRequest()
                },
            )
        },
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ConfirmCloseDialog(
            hazeState = HazeState(),
            onSaveClick = {},
            onDiscardClick = {},
            onDismissRequest = {},
        )
    }
}
