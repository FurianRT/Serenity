package com.furianrt.settings.internal.ui.composables

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.settings.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.ConfirmationDialogButton
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState

@Composable
internal fun WidgetErrorDialog(
    hazeState: HazeState,
    onSettingsClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isXiaomi = remember { Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) }

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
                title = stringResource(uiR.string.settings_title),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onSettingsClick()
                    onDismissRequest()
                },
            )
        },
        title = AnnotatedString(stringResource(R.string.settings_widget_error_dialog_title)),
        hint = if (isXiaomi) {
            AnnotatedString(stringResource(R.string.settings_xiaomi_widget_error_dialog_text))
        } else {
            AnnotatedString(stringResource(R.string.settings_xiaomi_widget_error_dialog_text))
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        WidgetErrorDialog(
            hazeState = HazeState(),
            onSettingsClick = {},
            onDismissRequest = {},
        )
    }
}
