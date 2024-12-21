package com.furianrt.toolspanel.api

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.furianrt.toolspanel.internal.LineContent
import com.furianrt.toolspanel.internal.RegularPanel
import com.furianrt.toolspanel.internal.SelectedPanel
import com.furianrt.toolspanel.internal.VoicePanel
import com.furianrt.uikit.extensions.clickableNoRipple
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

private enum class PanelMode {
    REGULAR,
    FORMATTING,
    VOICE_RECORD,
}

@Composable
fun ActionsPanel(
    textFieldState: TextFieldState,
    hazeState: HazeState,
    onSelectMediaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isVoiceRecordingActive by remember { mutableStateOf(false) }
    val hasMultiSelection by remember(textFieldState) {
        derivedStateOf {
            textFieldState.selection.min != textFieldState.selection.max
        }
    }
    val heightModifier = Modifier.height(48.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(heightModifier)
                .hazeChild(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                        noiseFactor = 0f,
                        blurRadius = 12.dp,
                    ),
                )
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .align(Alignment.BottomCenter),
        )
        AnimatedContent(
            targetState = when {
                isVoiceRecordingActive -> PanelMode.VOICE_RECORD
                hasMultiSelection -> PanelMode.FORMATTING
                else -> PanelMode.REGULAR
            },
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 90)))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            },
            label = "ActionsPanel",
        ) { targetState ->
            when (targetState) {
                PanelMode.REGULAR -> RegularPanel(
                    modifier = heightModifier.clickableNoRipple {},
                    textFieldState = textFieldState,
                    onSelectMediaClick = onSelectMediaClick,
                    onRecordVoiceClick = { isVoiceRecordingActive = true },
                )

                PanelMode.FORMATTING -> SelectedPanel(
                    modifier = heightModifier.clickableNoRipple {},
                    textFieldState = textFieldState,
                )

                PanelMode.VOICE_RECORD -> VoicePanel(
                    onDoneClick = { isVoiceRecordingActive = false },
                    lineContent = {
                        LineContent(
                            modifier = heightModifier.clickableNoRipple {},
                            onCancelClick = { isVoiceRecordingActive = false },
                        )
                    },
                )
            }
        }
    }
}
