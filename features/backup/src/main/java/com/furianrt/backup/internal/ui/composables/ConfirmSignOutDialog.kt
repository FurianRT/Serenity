package com.furianrt.backup.internal.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.furianrt.backup.R
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState

@Composable
internal fun ConfirmSignOutDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        hazeState = hazeState,
        title = stringResource(R.string.backup_confirm_sign_out_title),
        hint = stringResource(R.string.backup_confirm_sign_out_message),
        confirmText = stringResource(R.string.backup_sign_out_title),
        onDismissRequest = onDismissRequest,
        onConfirmClick = onConfirmClick,
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ConfirmSignOutDialog(
            hazeState = HazeState(),
            onDismissRequest = {},
        )
    }
}
