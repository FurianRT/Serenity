package com.furianrt.mediaselector.internal.ui.selector.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.R as uiR

@Composable
internal fun CheckBox(
    state: SelectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clickableNoRipple(onClick = onClick),
    ) {
        AnimatedVisibility(
            visible = state is SelectionState.Counter || state is SelectionState.Single,
            enter = scaleIn(initialScale = 0.7f) + fadeIn(),
            exit = scaleOut(targetScale = 0.5f) + fadeOut(),
        ) {
            Box(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    is SelectionState.Counter -> Counter(state = state)
                    is SelectionState.Single -> Single(modifier = Modifier.padding(2.dp))
                    is SelectionState.Default -> Unit
                }
            }
        }

        AnimatedVisibility(
            visible = state is SelectionState.Default,
            enter = fadeIn(),
            exit = fadeOut(tween(durationMillis = 200)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f), shape = CircleShape)
                    .border(width = 1.5.dp, color = Color.White, shape = CircleShape),
            )
        }
    }
}

@Composable
private fun Counter(
    state: SelectionState.Counter,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = state.order,
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

@Composable
private fun Single(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier,
        painter = painterResource(uiR.drawable.ic_action_done),
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        contentDescription = null,
    )
}

@Composable
@Preview
private fun PreviewCounter() {
    SerenityTheme {
        CheckBox(
            onClick = {},
            state = SelectionState.Counter(order = 1),
        )
    }
}

@Composable
@Preview
private fun PreviewSingle() {
    SerenityTheme {
        CheckBox(
            onClick = {},
            state = SelectionState.Single,
        )
    }
}

@Composable
@Preview
private fun PreviewDefault() {
    SerenityTheme {
        CheckBox(
            onClick = {},
            state = SelectionState.Default,
        )
    }
}
