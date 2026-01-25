package com.furianrt.notelist.internal.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.notelist.R
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import com.furianrt.uikit.R as uiR

private const val ANIM_OFFSET_DURATION = 400
private const val ANIM_BUTTON_SCROLL_DURATION = 350

@Composable
internal fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    needToHideNavigation: () -> Boolean = { false },
    needToShowScrollUpButton: () -> Boolean = { true },
    onScrollToTopClick: () -> Unit = {},
    onAddNoteClick: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
    val verticalBias by animateFloatAsState(
        targetValue = if (needToHideNavigation()) 1f else 0f,
        animationSpec = tween(durationMillis = ANIM_OFFSET_DURATION),
    )

    val navBarsHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(contentPadding)
            .graphicsLayer {
                val bottomPadding = contentPadding.calculateBottomPadding().toPx()
                translationY = (size.height + bottomPadding + navBarsHeight.toPx()) * verticalBias
            },
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ButtonScrollToTop(
            modifier = Modifier.padding(bottom = 4.dp),
            hazeState = hazeState,
            isVisible = needToShowScrollUpButton,
            onClick = onScrollToTopClick,
        )
        ButtonCreateNote(
            hazeState = hazeState,
            onClick = onAddNoteClick,
        )
    }
}

@Composable
private fun ButtonScrollToTop(
    hazeState: HazeState,
    isVisible: () -> Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible(),
        enter = fadeIn(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
        exit = fadeOut(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        tint = HazeTint(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                        noiseFactor = 0f,
                        blurRadius = 12.dp,
                    )
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(R.drawable.ic_scroll_up),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ButtonCreateNote(
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 250,
                delayMillis = 1500,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(56.dp)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .clip(CircleShape)
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    tint = HazeTint(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                    noiseFactor = 0f,
                    blurRadius = 12.dp,
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.padding(8.dp),
            painter = painterResource(uiR.drawable.ic_add),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    SerenityTheme {
        BottomNavigationBar(
            hazeState = HazeState(),
        )
    }
}
