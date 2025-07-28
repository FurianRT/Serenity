package com.furianrt.mediaview.internal.ui.composables

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.mediaview.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.components.defaultMenuItemColors
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch

@Composable
internal fun Menu(
    expanded: Boolean,
    hazeState: HazeState,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveMediaClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }
    DropdownMenu(
        modifier = Modifier.hazeEffect(
            state = hazeState,
            style = HazeDefaults.style(
                backgroundColor = Colors.Common.DarkGray,
                tint = HazeTint(Colors.Common.DarkGray.copy(alpha = 0.5f)),
                blurRadius = 12.dp,
            )
        ),
        offset = DpOffset(x = (-8).dp, y = 0.dp),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 0.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        val colors = defaultMenuItemColors().copy(
            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
            leadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        MenuItem(
            icon = painterResource(R.drawable.ic_download),
            text = stringResource(R.string.media_view_save_to_gallery),
            colors = colors,
            onClick = {
                onSaveMediaClick()
                onDismissRequest()
            }
        )
        MenuItem(
            icon = painterResource(uiR.drawable.ic_share),
            text = stringResource(uiR.string.action_share),
            colors = colors,
            onClick = {
                onShareClick()
                onDismissRequest()
            }
        )
        MenuItem(
            icon = painterResource(com.furianrt.uikit.R.drawable.ic_delete),
            text = stringResource(com.furianrt.uikit.R.string.action_delete),
            colors = colors,
            onClick = {
                onDeleteClick()
                onDismissRequest()
            }
        )
    }
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        Menu(
            expanded = true,
            hazeState = HazeState(),
            onDismissRequest = {},
            onShareClick = {},
            onDeleteClick = {},
            onSaveMediaClick = {},
        )
    }
}
