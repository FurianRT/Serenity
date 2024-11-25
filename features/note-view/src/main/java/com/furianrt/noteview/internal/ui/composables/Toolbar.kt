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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.uikit.R
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.components.ButtonEditAndDone
import com.furianrt.uikit.components.ButtonMenu
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch

private const val ANIM_DATE_VISIBILITY_DURATION = 250

@Composable
internal fun Toolbar(
    isInEditMode: Boolean,
    date: String?,
    dropDownHazeState: HazeState,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
    onBackButtonClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .height(ToolbarConstants.toolbarHeight)
            .fillMaxWidth(),
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
                    hazeState = dropDownHazeState,
                    onDeleteClick = onDeleteClick,
                    onShareClick = onShareClick,
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
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable(onClick = onClick),
            )
        }
        Text(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
            text = date,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun Menu(
    expanded: Boolean,
    hazeState: HazeState,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
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
            .hazeChild(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 12.dp,
                ),
            )
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f)),
        offset = DpOffset(x = (-8).dp, y = 0.dp),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.action_delete),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            },
            onClick = {
                onDeleteClick()
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.action_share),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            },
            onClick = {
                onShareClick()
                onDismissRequest()
            }
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
            dropDownHazeState = HazeState(),
        )
    }
}
