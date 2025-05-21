package com.furianrt.notecreate.internal.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.components.ButtonEditAndDone
import com.furianrt.uikit.components.ButtonMenu
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState

private const val ANIM_DATE_VISIBILITY_DURATION = 250

@Composable
internal fun Toolbar(
    date: String,
    isInEditMode: Boolean,
    isPinned: Boolean,
    dropDownHazeState: HazeState,
    onEditClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    onDateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .height(ToolbarConstants.toolbarHeight)
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .systemGestureExclusion(),
        contentAlignment = Alignment.Center,
    ) {
        ButtonBack(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onBackButtonClick,
        )
        DateLabel(
            date = date,
            isClickable = isInEditMode,
            onClick = onDateClick,
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ButtonEditAndDone(
                edit = isInEditMode,
                onClick = onEditClick,
            )
            Box {
                ButtonMenu(
                    onClick = { showDropDownMenu = true },
                )
                Menu(
                    expanded = showDropDownMenu,
                    isPinned = isPinned,
                    dropDownHazeState = dropDownHazeState,
                    onDeleteClick = onDeleteClick,
                    onPinClick = onPinClick,
                    onDismissRequest = { showDropDownMenu = false },
                )
            }
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

@PreviewWithBackground
@Composable
private fun ToolbarPreview() {
    SerenityTheme {
        Toolbar(
            date = "25 Nov 2024",
            isInEditMode = false,
            isPinned = false,
            dropDownHazeState = HazeState(),
            onEditClick = {},
            onBackButtonClick = {},
            onDateClick = {},
            onDeleteClick = {},
            onPinClick = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun ToolbarPreviewEditMode() {
    SerenityTheme {
        Toolbar(
            date = "25 Nov 2024",
            isInEditMode = true,
            isPinned = false,
            dropDownHazeState = HazeState(),
            onEditClick = {},
            onBackButtonClick = {},
            onDateClick = {},
            onDeleteClick = {},
            onPinClick = {},
        )
    }
}