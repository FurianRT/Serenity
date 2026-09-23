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
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

@Composable
internal fun PopUpMenu(
    expanded: Boolean,
    hazeState: HazeState,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current
    val colorScheme = MaterialTheme.colorScheme

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
            .hazeBlur(
                input = HazeInput.Sources(hazeState),
                style = HazeBlurStyle {
                    blurRadius(12.dp)
                    colorEffects(
                        listOf(HazeColorEffect.tint(colorScheme.surface.copy(alpha = 0.7f)))
                    )
                },
            )
            .background(MaterialTheme.colorScheme.background),
        containerColor = Color.Transparent,
        offset = DpOffset(x = 8.dp, y = -(8).dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
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