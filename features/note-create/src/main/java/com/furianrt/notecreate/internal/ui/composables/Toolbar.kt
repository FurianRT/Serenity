package com.furianrt.notecreate.internal.ui.composables

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private const val ANIM_DATE_VISIBILITY_DURATION = 250

@Composable
internal fun Toolbar(
    date: String,
    isInEditMode: Boolean,
    onEditClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(ToolbarConstants.toolbarHeight)
            .fillMaxWidth()
            .systemGestureExclusion(),
        contentAlignment = Alignment.Center,
    ) {
        DateLabel(
            date = date,
            isClickable = isInEditMode,
            onClick = onDateClick,
        )
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
            ButtonMenu(onClick = {})
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

@PreviewWithBackground
@Composable
private fun ToolbarPreview() {
    SerenityTheme {
        Toolbar(
            date = "25 Nov 2024",
            isInEditMode = false,
            onEditClick = {},
            onBackButtonClick = {},
            onDateClick = {},
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
            onEditClick = {},
            onBackButtonClick = {},
            onDateClick = {},
        )
    }
}