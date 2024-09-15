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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    title: String,
    hint: String,
    cancelText: String,
    confirmText: String,
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .hazeChild(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 20.dp,
                    ),
                )
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DialogButton(
                    title = cancelText,
                    textColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        onCancelClick()
                        onDismissRequest()
                    },
                )
                DialogButton(
                    title = confirmText,
                    textColor = MaterialTheme.colorScheme.errorContainer,
                    onClick = {
                        onConfirmClick()
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

@Composable
private fun DialogButton(
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
            cancelText = "Cancel",
            confirmText = "Confirm",
            onDismissRequest = {},
            hazeState = HazeState(),
        )
    }
}
