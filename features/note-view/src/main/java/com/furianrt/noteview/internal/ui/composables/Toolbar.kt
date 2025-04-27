package com.furianrt.noteview.internal.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.components.ButtonEditAndDone
import com.furianrt.uikit.components.ButtonMenu
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

private const val ANIM_DATE_VISIBILITY_DURATION = 250

@Composable
internal fun Toolbar(
    isInEditMode: Boolean,
    date: String?,
    isPinned: Boolean,
    dropDownHazeState: HazeState,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
    onBackButtonClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .height(ToolbarConstants.toolbarHeight)
            .fillMaxWidth()
            .systemGestureExclusion(),
        contentAlignment = Alignment.Center,
    ) {
        if (date != null) {
            DateLabel(
                isClickable = isInEditMode,
                date = date,
                onClick = onDateClick,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonBack(
                modifier = Modifier.padding(start = 4.dp),
                onClick = onBackButtonClick,
            )
            Spacer(modifier = Modifier.weight(1f))
            ButtonEditAndDone(
                edit = isInEditMode,
                onClick = onEditClick,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                ButtonMenu(onClick = { showDropDownMenu = true })
                Menu(
                    expanded = showDropDownMenu,
                    isPinned = isPinned,
                    hazeState = dropDownHazeState,
                    onDeleteClick = onDeleteClick,
                    onShareClick = onShareClick,
                    onPinClick = onPinClick,
                    onDismissRequest = { showDropDownMenu = false },
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
private fun DateLabel(
    date: String,
    isClickable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isClickable,
            enter = fadeIn(animationSpec = tween(ANIM_DATE_VISIBILITY_DURATION)),
            exit = fadeOut(animationSpec = tween(ANIM_DATE_VISIBILITY_DURATION)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
            )
        }
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 6.dp, horizontal = 12.dp),
            text = date,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun Menu(
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
                onDismissRequest()
                scope.launch {
                    delay(150)
                    onPinClick()
                }
            },
        )
        MenuItem(
            icon = painterResource(uiR.drawable.ic_share),
            text = stringResource(uiR.string.action_share),
            onClick = {
                onShareClick()
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

@PreviewWithBackground
@Composable
private fun ToolbarPreview() {
    SerenityTheme {
        Toolbar(
            isInEditMode = false,
            date = "30 Sep 1992",
            isPinned = false,
            dropDownHazeState = HazeState(),
        )
    }
}

@PreviewWithBackground
@Composable
private fun ToolbarPreviewEditMode() {
    SerenityTheme {
        Toolbar(
            isInEditMode = true,
            date = "30 Sep 1992",
            isPinned = false,
            dropDownHazeState = HazeState(),
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
