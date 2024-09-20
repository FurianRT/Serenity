package com.furianrt.uikit.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.debounceClickable

private const val ANIM_BUTTON_EDIT_DURATION = 350

@Composable
fun ButtonEditAndDone(
    onClick: () -> Unit,
    edit: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }
    var prevState by remember { mutableStateOf(edit) }

    LaunchedEffect(edit) {
        if (prevState == edit) {
            return@LaunchedEffect
        }
        prevState = edit
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
            imageVector = if (edit) {
                ImageVector.vectorResource(id = R.drawable.ic_action_done)
            } else {
                ImageVector.vectorResource(id = R.drawable.ic_action_edit)
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}