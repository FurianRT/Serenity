package com.furianrt.toolspanel.internal.ui.voice

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.toolspanel.api.VoiceRecord
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

private const val TAG = "VoicePanel"
private const val FAB_ANIM_DURATION = 1000
private const val VOLUME_MULTIPLIER = 2.5f

@Composable
internal fun VoicePanel(
    noteId: String,
    modifier: Modifier = Modifier,
    onRecordComplete: (record: VoiceRecord) -> Unit,
    onCancelRequest: () -> Unit,
) {
    val viewModel = hiltViewModel<VoiceViewModel, VoiceViewModel.Factory>(
        key = TAG + noteId,
        creationCallback = { it.create(noteId) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.onEvent(VoiceEvent.OnEnterComposition)
    }

    LifecycleStartEffect(Unit) {
        onStopOrDispose {
            viewModel.onEvent(VoiceEvent.OnScreenStopped)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is VoiceEffect.SendRecordCompleteEvent -> onRecordComplete(effect.record)
                    is VoiceEffect.CloseRecording -> onCancelRequest()
                }
            }
    }

    BackHandler {
        viewModel.onEvent(VoiceEvent.OnCancelClick)
    }

    VoicePanelContent(
        modifier = modifier,
        state = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun VoiceButtonDone(
    noteId: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<VoiceViewModel, VoiceViewModel.Factory>(
        key = TAG + noteId,
        creationCallback = { it.create(noteId) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    ButtonDoneContent(
        modifier = modifier,
        state = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ButtonDoneContent(
    state: VoiceUiState,
    onEvent: (event: VoiceEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ActionButtonInfiniteAnim")
    val dScale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = FAB_ANIM_DURATION,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    val scale by animateFloatAsState(
        targetValue = (state.volume * VOLUME_MULTIPLIER).coerceAtMost(1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    val addScale = if (state.isPaused) 0f else dScale
                    val result = (0.75f + 1.4f * scale + addScale).coerceAtMost(1.35f)
                    scaleX = result
                    scaleY = result
                }
                .size(84.dp)
                .alpha(0.5f)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        )

        FloatingActionButton(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    val result = 1f + 0.6f * scale
                    scaleX = result
                    scaleY = result
                },
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = { onEvent(VoiceEvent.OnDoneClick) },
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_send),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun VoicePanelContent(
    state: VoiceUiState,
    onEvent: (event: VoiceEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {},
    ) {
        Timer(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterStart),
            timer = state.duration,
            isPaused = state.isPaused,
            onClick = { onEvent(VoiceEvent.OnPauseClick) },
        )
        ButtonCancel(
            modifier = Modifier.align(Alignment.Center),
            onClick = { onEvent(VoiceEvent.OnCancelClick) },
        )
    }
}

@Composable
private fun Timer(
    timer: String,
    isPaused: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.bodySmall
    val density = LocalDensity.current
    val timeWidth = remember {
        density.run {
            textMeasurer.measure(text = "88:88:8", style = style, maxLines = 1).size.width.toDp()
        }
    }
    Row(
        modifier = modifier
            .clickableNoRipple {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                onClick()
            }
            .padding(start = 6.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            modifier = Modifier.width(timeWidth),
            text = timer,
            style = style,
        )
        AnimatedContent(
            modifier = Modifier
                .size(22.dp)
                .alpha(0.5f),
            targetState = isPaused,
        ) { targetState ->
            if (targetState) {
                Icon(
                    painter = painterResource(uiR.drawable.ic_play),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            } else {
                Icon(
                    painter = painterResource(uiR.drawable.ic_pause),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
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
private fun VoicePanelPreview() {
    SerenityTheme {
        VoicePanelContent(
            state = VoiceUiState(
                isPaused = false,
                duration = "0:00:0",
                volume = 0.5f,
            ),
            onEvent = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun ButtonPreview() {
    SerenityTheme {
        ButtonDoneContent(
            state = VoiceUiState(
                isPaused = false,
                duration = "0:00:0",
                volume = 0.5f,
            ),
            onEvent = {},
        )
    }
}
