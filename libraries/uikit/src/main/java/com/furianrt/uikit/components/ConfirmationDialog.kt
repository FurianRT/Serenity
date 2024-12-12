package com.furianrt.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.uikit.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch

@Composable
fun ConfirmationDialog(
    cancelText: String = stringResource(R.string.action_cancel),
    confirmText: String = stringResource(R.string.action_confirm),
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hideOnLock: Boolean = true,
    title: AnnotatedString? = null,
    hint: AnnotatedString? = null,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        cancelButton = {
            ConfirmationDialogButton(
                title = cancelText,
                textColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    onCancelClick()
                    onDismissRequest()
                },
            )
        },
        confirmButton = {
            ConfirmationDialogButton(
                title = confirmText,
                textColor = MaterialTheme.colorScheme.errorContainer,
                onClick = {
                    onConfirmClick()
                    onDismissRequest()
                },
            )
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
        hideOnLock = hideOnLock,
        title = title,
        hint = hint,
    )
}

@Composable
fun ConfirmationDialog(
    cancelText: String = stringResource(R.string.action_cancel),
    confirmText: String = stringResource(R.string.action_confirm),
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hideOnLock: Boolean = true,
    title: String? = null,
    hint: String? = null,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        cancelButton = {
            ConfirmationDialogButton(
                title = cancelText,
                textColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    onCancelClick()
                    onDismissRequest()
                },
            )
        },
        confirmButton = {
            ConfirmationDialogButton(
                title = confirmText,
                textColor = MaterialTheme.colorScheme.errorContainer,
                onClick = {
                    onConfirmClick()
                    onDismissRequest()
                },
            )
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        hideOnLock = hideOnLock,
        title = title?.let {
            buildAnnotatedString { append(it) }
        },
        hint = hint?.let {
            buildAnnotatedString { append(it) }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    cancelButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hideOnLock: Boolean = true,
    title: AnnotatedString? = null,
    hint: AnnotatedString? = null,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    if (hideOnLock) {
        LifecycleStartEffect(Unit) {
            scope.launch {
                if (!auth.isAuthorized()) {
                    onDismissRequest()
                }
            }
            onStopOrDispose {}
        }
    }
    BasicAlertDialog(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .hazeChild(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 20.dp,
                ),
            )
            .background(MaterialTheme.colorScheme.surfaceTint)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (title != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (hint != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = hint,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cancelButton()
                confirmButton()
            }
        }
    }
}

@Composable
fun ConfirmationDialogButton(
    title: String,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = title,
            color = textColor,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ConfirmationDialog(
            title = "Test title",
            hint = "Test hint",
            onDismissRequest = {},
            hazeState = HazeState(),
        )
    }
}
