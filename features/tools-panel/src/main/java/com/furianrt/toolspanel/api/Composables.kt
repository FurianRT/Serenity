package com.furianrt.toolspanel.api

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.furianrt.toolspanel.internal.RegularPanelInternal
import com.furianrt.toolspanel.internal.SelectedPanelInternal
import com.furianrt.uikit.extensions.clickableNoRipple
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

@Composable
fun ActionsPanel(
    textFieldState: TextFieldState,
    hazeState: HazeState,
    onSelectMediaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasMultiSelection by remember(textFieldState) {
        derivedStateOf {
            textFieldState.selection.min != textFieldState.selection.max
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
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
            .clickableNoRipple {},
    ) {
        AnimatedContent(
            targetState = hasMultiSelection,
            label = "ActionsPanel"
        ) { targetState ->
            if (targetState) {
                SelectedPanelInternal(
                    textFieldState = textFieldState,
                )
            } else {
                RegularPanelInternal(
                    textFieldState = textFieldState,
                    onSelectMediaClick = onSelectMediaClick,
                )
            }
        }
    }
}
