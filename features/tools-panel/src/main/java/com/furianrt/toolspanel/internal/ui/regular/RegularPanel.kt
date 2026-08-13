package com.furianrt.toolspanel.internal.ui.regular

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.core.orFalse
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.api.ToolsPanelConstants
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun RegularPanel(
    titleState: NoteTitleState?,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    onFontStyleClick: () -> Unit = {},
    onStickersClick: () -> Unit = {},
    onBulletListClick: () -> Unit = {},
    onBackgroundClick: () -> Unit = {},
    onSelectMediaClick: () -> Unit = {},
    onRecordVoiceClick: () -> Unit = {},
) {
    val isContentWidthFit by remember {
        derivedStateOf { !scrollState.canScrollForward && !scrollState.canScrollBackward }
    }
    Row(
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(scrollState)
            .clickableNoRipple {},
        horizontalArrangement = if (isContentWidthFit) {
            Arrangement.SpaceEvenly
        } else {
            Arrangement.Start
        },
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
            onClick = onFontStyleClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_font),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onBackgroundClick,
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(1.dp),
                painter = painterResource(R.drawable.ic_panel_background),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onSelectMediaClick,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_camera),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = onRecordVoiceClick,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_microphone),
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
            modifier = Modifier.height(ToolsPanelConstants.PANEL_HEIGHT),
            titleState = NoteTitleState(
                fontFamily = UiNoteFontFamily.NotoSans,
                fontSize = 16.sp,
            ),
            scrollState = rememberScrollState(),
        )
    }
}
