package com.furianrt.noteview.internal.ui.container.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.debounceClickable
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

private const val ANIM_BUTTON_EDIT_DURATION = 350
private const val ANIM_DATE_VISIBILITY_DURATION = 250

@Composable
internal fun Toolbar(
    isInEditMode: Boolean,
    date: String?,
    onEditClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(64.dp)
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
                isInEditMode = isInEditMode,
                onClick = onEditClick,
            )
            Spacer(modifier = Modifier.width(8.dp))
            ButtonMenu(onClick = {})
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
private fun ButtonMenu(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.clickableWithScaleAnim(
            maxScale = 1.2f,
            indication = ripple(bounded = false, radius = 20.dp),
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = modifier.padding(8.dp),
            imageVector = ImageVector.vectorResource(id = uiR.drawable.ic_action_menu),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ButtonEditAndDone(
    onClick: () -> Unit,
    isInEditMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }
    var prevState by remember { mutableStateOf(isInEditMode) }

    LaunchedEffect(isInEditMode) {
        if (prevState == isInEditMode) {
            return@LaunchedEffect
        }
        prevState = isInEditMode
        scale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(durationMillis = ANIM_BUTTON_EDIT_DURATION / 2),
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = ANIM_BUTTON_EDIT_DURATION / 2),
        )
    }

    Box(
        modifier = modifier.debounceClickable(
            indication = ripple(bounded = false, radius = 20.dp),
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            imageVector = if (isInEditMode) {
                ImageVector.vectorResource(id = uiR.drawable.ic_action_done)
            } else {
                ImageVector.vectorResource(id = uiR.drawable.ic_action_edit)
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
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
            isInEditMode = false,
            date = "30 Sep 1992",
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
            isInEditMode = true,
            date = "30 Sep 1992",
            onEditClick = {},
            onBackButtonClick = {},
            onDateClick = {},
        )
    }
}
