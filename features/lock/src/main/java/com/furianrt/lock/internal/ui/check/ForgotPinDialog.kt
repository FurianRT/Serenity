package com.furianrt.lock.internal.ui.check

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.furianrt.lock.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState

@Composable
internal fun ForgotPinDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        hint = stringResource(R.string.pin_recovery_dialog_title),
        confirmText = stringResource(uiR.string.action_send),
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
        onConfirmClick = onConfirmClick,
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ForgotPinDialog(
            onDismissRequest = {},
            hazeState = HazeState(),
        )
    }
}
