package com.furianrt.uikit.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.launch

private const val SCALE_ANIM_DURATION = 150

@Composable
fun ActionButton(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    FloatingActionButton(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = CircleShape,
        onClick = {
            if (scale.isRunning) {
                return@FloatingActionButton
            }
            scope.launch {
                scale.animateTo(
                    targetValue = 1.05f,
                    animationSpec = tween(SCALE_ANIM_DURATION / 2),
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(SCALE_ANIM_DURATION / 2),
                )
            }
            onClick()
        },
    ) {
        Icon(
            painter = icon,
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        ActionButton(
            icon = painterResource(R.drawable.ic_action_edit),
            onClick = {},
        )
    }
}
