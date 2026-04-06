package com.furianrt.toolspanel.internal.ui.background.custom.composables

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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch

@Composable
internal fun PopUpMenu(
    expanded: Boolean,
    hazeState: HazeState,
    onDeleteClick: () -> Unit,
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
        modifier = Modifier
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 12.dp,
                )
            )
            .background(MaterialTheme.colorScheme.background),
        containerColor = Color.Transparent,
        offset = DpOffset(x = 8.dp, y = -(8).dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
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