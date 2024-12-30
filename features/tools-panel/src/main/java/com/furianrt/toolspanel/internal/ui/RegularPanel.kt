package com.furianrt.toolspanel.internal.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.furianrt.toolspanel.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun RegularPanel(
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    onSelectMediaClick: () -> Unit = {},
    onRecordVoiceClick: () -> Unit = {},
    onFontStyleClick: () -> Unit = {},
) {
    val undoRedoState = remember(textFieldState) { textFieldState.undoState }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = undoRedoState.canUndo,
            onClick = { undoRedoState.undo() },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_undo),
                contentDescription = null,
            )
        }
        IconButton(
            onClick = onFontStyleClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_font),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onSelectMediaClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_camera),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = { },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_stickers),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onRecordVoiceClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_microphone),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            enabled = undoRedoState.canRedo,
            onClick = { undoRedoState.redo() },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_redo),
                contentDescription = null,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun RegularPanelPreview() {
    SerenityTheme {
        RegularPanel(
            textFieldState = rememberTextFieldState(),
        )
    }
}
