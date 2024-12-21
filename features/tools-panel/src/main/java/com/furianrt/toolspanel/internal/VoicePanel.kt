package com.furianrt.toolspanel.internal

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun VoicePanel(
    modifier: Modifier = Modifier,
    onDoneClick: () -> Unit = {},
    lineContent: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        lineContent()
        ButtonDone(
            modifier = Modifier
                .offset(x = 20.dp, y = 20.dp)
                .align(Alignment.CenterEnd),
            onClick = onDoneClick,
        )
    }
}

@Composable
internal fun BoxScope.LineContent(
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit = {},
) {
    var isPlaying by remember { mutableStateOf(true) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
    ) {
        Timer(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterStart),
            timer = "0:16.7",
            isPlaying = isPlaying,
            onClick = { isPlaying = !it },
        )
        ButtonCancel(
            modifier = Modifier.align(Alignment.Center),
            onClick = onCancelClick,
        )
    }
}

@Composable
private fun Timer(
    timer: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: (isPlaying: Boolean) -> Unit,
) {
    val view = LocalView.current
    Row(
        modifier = modifier
            .clickableNoRipple {
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                onClick(isPlaying)
            }
            .padding(start = 6.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = timer,
            style = MaterialTheme.typography.bodyMedium,
        )
        AnimatedContent(
            modifier = Modifier
                .size(22.dp)
                .alpha(0.5f),
            targetState = isPlaying,
            label = "PlayAnim",
        ) { targetState ->
            if (targetState) {
                Icon(
                    painter = painterResource(uiR.drawable.ic_pause),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            } else {
                Icon(
                    painter = painterResource(uiR.drawable.ic_play),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun ButtonDone(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ActionButtonInfiniteAnim")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ActionButtonScaleAnim",
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .size(84.dp)
                .alpha(0.5f)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        )

        FloatingActionButton(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = onClick,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_send),
                tint = Color.Unspecified,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ButtonCancel(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            text = stringResource(uiR.string.action_cancel),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@PreviewWithBackground
@Composable
private fun SelectedPanelPreview() {
    SerenityTheme {
        VoicePanel {
            LineContent()
        }
    }
}
