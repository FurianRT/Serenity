package com.furianrt.noteview.internal.ui.container.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.assistant.api.AssistantLogo
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.debounceClickable
import me.onebone.toolbar.CollapsingToolbarScope
import com.furianrt.uikit.R as uiR

private const val ANIM_BUTTON_EDIT_DURATION = 350

@Composable
internal fun CollapsingToolbarScope.Toolbar(
    isInEditMode: () -> Boolean,
    date: () -> String,
    onEditClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .pin()
            .height(64.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ButtonBack(
            modifier = Modifier.padding(start = 4.dp),
            onClick = onBackButtonClick,
        )
        DateLabel(
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(1f),
            date = date(),
        )
        ButtonEditAndDone(
            modifier = Modifier.padding(end = 28.dp),
            isInEditMode = isInEditMode,
            onClick = onEditClick,
        )
        AssistantLogo(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(28.dp),
            onClick = {},
        )
        ButtonMenu(
            onClick = {},
        )
    }
}

@Composable
private fun ButtonMenu(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Icon(
        modifier = modifier
            .padding(8.dp)
            .clickableWithScaleAnim(
                maxScale = 1.2f,
                indication = rememberRipple(bounded = false),
                onClick = onClick,
            ),
        painter = painterResource(id = uiR.drawable.ic_action_menu),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun ButtonEditAndDone(
    onClick: () -> Unit,
    isInEditMode: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }
    var prevState by remember { mutableStateOf(isInEditMode()) }

    LaunchedEffect(isInEditMode()) {
        if (prevState == isInEditMode()) {
            return@LaunchedEffect
        }
        prevState = isInEditMode()
        scale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(durationMillis = ANIM_BUTTON_EDIT_DURATION / 2),
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = ANIM_BUTTON_EDIT_DURATION / 2),
        )
    }

    Icon(
        modifier = modifier
            .debounceClickable(
                indication = rememberRipple(bounded = false),
                onClick = onClick,
            )
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        painter = if (isInEditMode()) {
            painterResource(id = uiR.drawable.ic_action_done)
        } else {
            painterResource(id = uiR.drawable.ic_action_edit)
        },
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
private fun ButtonBack(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = uiR.drawable.ic_arrow_back),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun DateLabel(
    date: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = date,
        style = MaterialTheme.typography.bodyMedium,
    )
}
