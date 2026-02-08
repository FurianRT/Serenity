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
import androidx.compose.ui.res.painterResource
import com.furianrt.core.orFalse
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun RegularPanel(
    titleState: NoteTitleState?,
    modifier: Modifier = Modifier,
    onFontStyleClick: () -> Unit = {},
    onStickersClick: () -> Unit = {},
    onBulletListClick: () -> Unit = {},
    onAttachClick: () -> Unit = {},
    onBackgroundClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = titleState?.canUndo.orFalse(),
            onClick = { titleState?.undo() },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.surfaceContainerLow,
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_undo),
                contentDescription = null,
            )
        }
        IconButton(
            onClick = onBulletListClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_bullet_list),
                contentDescription = null,
                tint = if (titleState == null) {
                    MaterialTheme.colorScheme.surfaceContainerLow
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
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
            onClick = onStickersClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_stickers),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onAttachClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_attach),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onBackgroundClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_background),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            enabled = titleState?.canRedo.orFalse(),
            onClick = { titleState?.redo() },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
                fontFamily = UiNoteFontFamily.NotoSans,
            ),
        )
    }
}
