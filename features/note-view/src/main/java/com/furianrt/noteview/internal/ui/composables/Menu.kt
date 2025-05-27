package com.furianrt.noteview.internal.ui.composables

import androidx.compose.foundation.background
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
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch

@Composable
internal fun Menu(
    expanded: Boolean,
    isPinned: Boolean,
    hazeState: HazeState,
    onDeleteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
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
        modifier = Modifier
            .hazeChild(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 12.dp,
                ),
            )
            .background(MaterialTheme.colorScheme.tertiaryContainer),
        offset = DpOffset(x = (-8).dp, y = 0.dp),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        MenuItem(
            icon = if (isPinned) {
                painterResource(uiR.drawable.ic_unpin)
            } else {
                painterResource(uiR.drawable.ic_pin)
            },
            text =  if (isPinned) {
                stringResource(uiR.string.action_unpin)
            } else {
                stringResource(uiR.string.action_pin)
            },
            onClick = {
                onPinClick()
                onDismissRequest()
            },
        )
       /* MenuItem(
            icon = painterResource(uiR.drawable.ic_share),
            text = stringResource(uiR.string.action_share),
            onClick = {
                onShareClick()
                onDismissRequest()
            },
        )*/
        MenuItem(
            icon = painterResource(uiR.drawable.ic_delete),
            text = stringResource(uiR.string.action_delete),
            onClick = {
                onDeleteClick()
                onDismissRequest()
            },
        )
    }
}

@Preview(heightDp = 200, widthDp = 150)
@Composable
private fun MenuPreview() {
    SerenityTheme {
        Menu(
            expanded = true,
            isPinned = false,
            hazeState = HazeState(),
        )
    }
}
