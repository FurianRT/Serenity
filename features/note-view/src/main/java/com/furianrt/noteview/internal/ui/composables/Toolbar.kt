package com.furianrt.noteview.internal.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        text = date,
        style = MaterialTheme.typography.bodyMedium,
    )
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
