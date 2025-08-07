package com.furianrt.toolspanel.internal.ui.regular

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import com.furianrt.core.orFalse
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun RegularPanel(
    titleState: NoteTitleState?,
    modifier: Modifier = Modifier,
    onSelectMediaClick: () -> Unit = {},
    onRecordVoiceClick: () -> Unit = {},
    onFontStyleClick: () -> Unit = {},
    onStickersClick: () -> Unit = {},
    onBulletListClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = titleState?.canUndo.orFalse(),
            onClick = { titleState?.undo() },
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
            modifier = Modifier.alpha(if (titleState == null) 0.5f else 1f),
            onClick = onBulletListClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_bullet_list),
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
            onClick = onStickersClick,
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
            enabled = titleState?.canRedo.orFalse(),
            onClick = { titleState?.redo() },
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
            titleState = NoteTitleState(
                fontFamily = UiNoteFontFamily.QuickSand,
            ),
        )
    }
}
