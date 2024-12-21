package com.furianrt.mediaselector.internal.ui.selector.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.R
import com.furianrt.uikit.components.ActionButton
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlin.math.max

private const val ACTION_PANEL_ANIM_DURATION = 250

@Composable
internal fun BottomPanel(
    selectedCount: Int,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onSendClick: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = selectedCount > 0,
            enter = fadeIn(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + slideIn(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                initialOffset = { IntOffset(0, it.height) },
            ),
            exit = fadeOut(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + slideOut(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                targetOffset = { IntOffset(0, it.height) },
            )
        ) {
            SelectedCountHint(
                modifier = Modifier
                    .hazeChild(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            blurRadius = 12.dp,
                        ),
                    )
                    .navigationBarsPadding(),
                count = selectedCount,
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .wrapContentSize()
                .padding(end = 16.dp, bottom = 20.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomEnd),
            visible = selectedCount > 0,
            enter = fadeIn(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
            ),
            exit = fadeOut(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + scaleOut(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                targetScale = 0.5f,
            )
        ) {
            ActionButton(
                icon = painterResource(com.furianrt.uikit.R.drawable.ic_send),
                onClick = onSendClick,
            )
        }
    }
}

@Composable
private fun SelectedCountHint(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.media_selector_apply_button_title, max(1, count)),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        BottomPanel(
            selectedCount = 3,
            hazeState = HazeState(),
        )
    }
}
