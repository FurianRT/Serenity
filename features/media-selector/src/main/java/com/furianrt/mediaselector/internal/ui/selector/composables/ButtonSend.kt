package com.furianrt.mediaselector.internal.ui.selector.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtLeast
import com.furianrt.uikit.components.ActionButton
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import com.furianrt.uikit.R as uiR

@Composable
internal fun ButtonSend(
    selectedCount: Int,
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeColor: Color = MaterialTheme.colorScheme.surface,
) {
    val counterScale = remember { Animatable(1f) }

    LaunchedEffect(selectedCount.fastCoerceAtLeast(1)) {
        counterScale.animateTo(
            targetValue = 0.92f,
            animationSpec = tween(durationMillis = 30, easing = LinearEasing),
        )
        counterScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 70, easing = LinearEasing),
        )
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd,
    ) {
        ActionButton(
            modifier = Modifier.size(64.dp),
            icon = painterResource(uiR.drawable.ic_send),
            elevation = 0.dp,
            onClick = onClick,
        )
        AnimatedVisibility(
            visible = selectedCount > 0,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)),
        ) {
            Box(
                modifier = Modifier
                    .offset(x = 2.dp, y = 2.dp)
                    .graphicsLayer {
                        scaleX = counterScale.value
                        scaleY = counterScale.value
                    }
                    .size(28.dp)
                    .clip(CircleShape)
                    .hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = hazeColor,
                            tint = HazeTint(hazeColor.copy(alpha = 0.4f)),
                            blurRadius = 12.dp,
                        )
                    )
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Box(
                modifier = Modifier
                    .offset(x = 4.dp, y = 4.dp)
                    .graphicsLayer {
                        scaleX = counterScale.value
                        scaleY = counterScale.value
                    }
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                    ),
            ) {
                AnimatedContent(
                    targetState = selectedCount.fastCoerceAtLeast(1),
                    transitionSpec = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        ).togetherWith(
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            )
                        )
                    },
                ) { targetState ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = targetState.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ButtonSend(
            selectedCount = 2,
            hazeState = rememberHazeState(),
            onClick = {},
        )
    }
}
