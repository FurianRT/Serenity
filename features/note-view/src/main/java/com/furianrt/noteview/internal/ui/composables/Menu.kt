package com.furianrt.noteview.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.components.SerenityPlusIcon
import com.furianrt.uikit.theme.LocalSerenityPlus
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

@Composable
internal fun Menu(
    expanded: Boolean,
    isPinned: Boolean,
    hazeState: HazeState,
    onDeleteClick: () -> Unit = {},
    onExportPdfClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
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
        MenuItem(
            leadingIcon = {
                Icon(
                    painter = painterResource(uiR.drawable.ic_export_file),
                    contentDescription = null,
                )
            },
            trailingIcon = if (LocalSerenityPlus.current) {
                null
            } else {
                {
                    SerenityPlusIcon(
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(uiR.string.action_export_pdf),
                    style = MaterialTheme.typography.titleSmall,
                )
            },
            onClick = {
                onExportPdfClick()
                onDismissRequest()
            },
        )
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
