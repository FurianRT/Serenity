package com.furianrt.notelistui.composables

import android.net.Uri
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notelistui.entities.UiNoteContent.Voice
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.debounceClickable
import com.furianrt.uikit.extensions.toTimeString
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlin.random.Random
import com.furianrt.uikit.R as uiR

private const val VOLUME_MULTIPLIER = 2.5f
private const val ANIM_EDIT_MODE_DURATION = 250
private const val ANIM_PLAY_BUTTON_DURATION = 350
private const val PLAY_BUTTON_DEBOUNCE = 200L

@Composable
fun NoteContentVoice(
    voice: Voice,
    isPlaying: Boolean,
    isRemovable: Boolean,
    isPayable: Boolean,
    modifier: Modifier = Modifier,
    onRemoveClick: (voice: Voice) -> Unit = {},
    onPlayClick: (voice: Voice) -> Unit = {},
    onProgressSelected: (voice: Voice, value: Float) -> Unit = { _, _ -> },
) {
    Box(modifier = modifier) {
        VoiceContent(
            modifier = Modifier.padding(top = 4.dp, end = 2.dp),
            voice = voice,
            isPlaying = isPlaying,
            isPayable = isPayable,
            onPlayClick = onPlayClick,
            onProgressSelected = onProgressSelected,
        )
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = isRemovable,
            enter = fadeIn(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
            exit = fadeOut(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
        ) {
            ButtonRemove(
                onClick = { onRemoveClick(voice) },
            )
        }
    }
}

@Composable
private fun VoiceContent(
    voice: Voice,
    isPlaying: Boolean,
    isPayable: Boolean,
    onPlayClick: (voice: Voice) -> Unit,
    onProgressSelected: (voice: Voice, value: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var wasStarted by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier.width(if (isPayable) 240.dp else 200.dp),
        shape = RoundedCornerShape(24),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isPayable) {
                ButtonPlay(
                    isPaused = !isPlaying,
                    onClick = {
                        if (!isPlaying) {
                            wasStarted = true
                        }
                        onPlayClick(voice)
                    },
                )
            } else {
                Spacer(Modifier.width(6.dp))
            }
            VoiceSlider(
                modifier = Modifier.weight(1f),
                enabled = isPayable,
                value = voice.progress,
                volume = voice.volume,
                onValueChangeFinished = { onProgressSelected(voice, it) },
            )
            Timer(
                time = if (!wasStarted || voice.progress == 0f) {
                    voice.duration.toTimeString()
                } else {
                    voice.currentDuration.toTimeString()
                },
            )
        }
    }
}

@Composable
private fun ButtonPlay(
    isPaused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateIntAsState(
        targetValue = if (isPaused) 0 else 90,
        animationSpec = tween(ANIM_PLAY_BUTTON_DURATION)
    )

    val scaleModifier = if (isPaused) {
        Modifier.scale(0.7f)
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "ActionButtonInfiniteAnim")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
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
        Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .then(scaleModifier),
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .debounceClickable(
                    debounceInterval = PLAY_BUTTON_DEBOUNCE,
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Crossfade(
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { rotationZ = rotation.toFloat() },
                targetState = isPaused,
                animationSpec = tween(200),
                label = "PlayAnim",
            ) { targetState ->
                if (targetState) {
                    Icon(
                        painter = painterResource(uiR.drawable.ic_play),
                        tint = MaterialTheme.colorScheme.tertiaryContainer
                            .compositeOver(MaterialTheme.colorScheme.surface)
                            .compositeOver(MaterialTheme.colorScheme.tertiary),
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        modifier = Modifier.rotate(-90f),
                        painter = painterResource(uiR.drawable.ic_pause),
                        tint = MaterialTheme.colorScheme.tertiaryContainer
                            .compositeOver(MaterialTheme.colorScheme.surface)
                            .compositeOver(MaterialTheme.colorScheme.tertiary),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun Timer(
    time: String,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.bodySmall
    val density = LocalDensity.current
    val timeWidth = remember {
        density.run {
            textMeasurer.measure(text = "88:88", style = style, maxLines = 1).size.width.toDp()
        }
    }
    Text(
        modifier = modifier.width(timeWidth),
        text = time,
        style = style,
        maxLines = 1,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceSlider(
    value: Float,
    volume: ImmutableList<Float>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (value: Float) -> Unit,
) {
    var isThumbDragging by remember { mutableStateOf(false) }
    var currentPosition by remember(if (isThumbDragging) isThumbDragging else value) {
        mutableFloatStateOf(value)
    }
    val view = LocalView.current
    val sliderInteractionSource = remember { MutableInteractionSource() }
    LaunchedEffect(sliderInteractionSource) {
        sliderInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> isThumbDragging = true
                is DragInteraction.Stop -> isThumbDragging = false
            }
        }
    }

    Slider(
        modifier = modifier.applyIf(!enabled) { Modifier.height(32.dp) },
        enabled = enabled,
        value = currentPosition,
        onValueChange = { position ->
            view.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
            currentPosition = position
        },
        onValueChangeFinished = { onValueChangeFinished(currentPosition) },
        valueRange = 0f..1f,
        interactionSource = sliderInteractionSource,
        track = { Track(volume = volume, progress = it.value) },
        thumb = {},
    )
}

@Composable
private fun Track(
    volume: ImmutableList<Float>,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val totalColumns = volume.size
        val fullColumns = (totalColumns * progress).toInt()
        val partialProgress = totalColumns * progress - fullColumns

        volume.forEachIndexed { index, value ->
            val adjustedHeight = (value * VOLUME_MULTIPLIER).coerceAtMost(1f)
            val color = when {
                index < fullColumns -> MaterialTheme.colorScheme.primary
                index == fullColumns -> {
                    val color = MaterialTheme.colorScheme.onTertiaryContainer
                    MaterialTheme.colorScheme.onTertiaryContainer.copy(
                        alpha = color.alpha + (1f - color.alpha) * partialProgress
                    )
                }

                else -> MaterialTheme.colorScheme.onTertiaryContainer
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(22.dp * adjustedHeight + 2.dp)
                    .background(color = color, shape = RoundedCornerShape(32.dp)),
            )
        }
    }
}

@Composable
private fun ButtonRemove(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .padding(2.dp)
                .size(16.dp),
            painter = painterResource(uiR.drawable.ic_close_small),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
@PreviewWithBackground
private fun PreviewWithPlayButton() {
    SerenityTheme {
        NoteContentVoice(
            voice = Voice(
                id = "",
                uri = Uri.EMPTY,
                duration = 10000,
                progress = 0.5f,
                volume = buildImmutableList {
                    repeat(42) {
                        add(Random.nextDouble(0.0, 0.7).toFloat())
                    }
                },
            ),
            isRemovable = true,
            isPlaying = false,
            isPayable = true,
        )
    }
}

@Composable
@PreviewWithBackground
private fun PreviewWithoutPlayButton() {
    SerenityTheme {
        NoteContentVoice(
            voice = Voice(
                id = "",
                uri = Uri.EMPTY,
                duration = 10000,
                progress = 0.5f,
                volume = buildImmutableList {
                    repeat(42) {
                        add(Random.nextDouble(0.0, 0.7).toFloat())
                    }
                },
            ),
            isRemovable = false,
            isPlaying = false,
            isPayable = false,
        )
    }
}
