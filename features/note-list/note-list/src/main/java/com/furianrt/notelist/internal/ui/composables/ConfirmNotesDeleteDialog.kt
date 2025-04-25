package com.furianrt.notelist.internal.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.notelist.R
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState

@Composable
internal fun ConfirmNotesDeleteDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        title = stringResource(R.string.notes_list_delete_notes_warning_title),
        hint = stringResource(R.string.notes_list_delete_notes_warning_body),
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
            hazeState = HazeState(),
            onDismissRequest = {},
        )
    }
}
