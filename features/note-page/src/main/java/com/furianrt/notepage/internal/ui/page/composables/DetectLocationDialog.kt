package com.furianrt.notepage.internal.ui.page.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.furianrt.notepage.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.ConfirmationDialogButton
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState

@Composable
internal fun DetectLocationDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        cancelButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_no),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onDismissRequest,
            )
        },
        confirmButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_yes),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onConfirmClick()
                    onDismissRequest()
                },
            )
        },
        hint = AnnotatedString(stringResource(R.string.note_auto_detect_location_title)),
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        DetectLocationDialog(
            hazeState = HazeState(),
            onDismissRequest = {},
        )
    }
}