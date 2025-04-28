package com.furianrt.uikit.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.uikit.R
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState

@Composable
fun ConfirmNotesDeleteDialog(
    notesCount: Int,
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        title = stringResource(R.string.delete_notes_warning_title),
        hint = if (notesCount > 1) {
            stringResource(R.string.delete_notes_warning_body)
        } else {
            stringResource(R.string.delete_note_warning_body)
        },
        confirmText = stringResource(R.string.action_delete),
        hazeState = hazeState,
        onConfirmClick = onConfirmClick,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
@Preview
private fun Preview(modifier: Modifier = Modifier) {
    SerenityTheme {
        ConfirmNotesDeleteDialog(
            notesCount = 3,
            hazeState = HazeState(),
            onDismissRequest = {},
        )
    }
}