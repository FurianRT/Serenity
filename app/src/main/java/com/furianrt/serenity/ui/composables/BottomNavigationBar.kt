package com.furianrt.serenity.ui.composables

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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.serenity.R
import com.furianrt.uikit.components.ActionButton
import com.furianrt.uikit.theme.SerenityTheme

private const val ANIM_OFFSET_DURATION = 500
private const val ANIM_BUTTON_SCROLL_DURATION = 350

@Composable
internal fun BottomNavigationBar(
    needToHideNavigation: () -> Boolean = { false },
    needToShowScrollUpButton: () -> Boolean = { true },
    modifier: Modifier = Modifier,
    onScrollToTopClick: () -> Unit = {},
    onAddNoteClick: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
    val verticalBias by animateFloatAsState(
        targetValue = if (needToHideNavigation()) 1f else 0f,
        animationSpec = tween(durationMillis = ANIM_OFFSET_DURATION),
        label = "BottomNavigationBarOffsetAnim",
    )

    val navBarsHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val infiniteTransition = rememberInfiniteTransition(label = "ActionButtonInfiniteAnim")
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
        label = "ActionButtonScaleAnim",
    )

    Row(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(contentPadding)
            .graphicsLayer {
                val bottomPadding = contentPadding
                    .calculateBottomPadding()
                    .toPx()
                translationY = (size.height + bottomPadding + navBarsHeight.toPx()) * verticalBias
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ButtonScrollToTop(
            modifier = Modifier.padding(top = 4.dp),
            isVisible = needToShowScrollUpButton,
            onClick = onScrollToTopClick,
        )
        ActionButton(
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            icon = painterResource(R.drawable.ic_fab_add),
            onClick = onAddNoteClick,
        )
    }
}

@Composable
private fun ButtonScrollToTop(
    isVisible: () -> Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var buttonHeight by remember { mutableIntStateOf(0) }
    AnimatedVisibility(
        modifier = modifier.onGloballyPositioned { buttonHeight = it.size.height },
        visible = isVisible(),
        enter = fadeIn(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
        exit = fadeOut(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
    ) {

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.padding(7.dp),
                painter = painterResource(R.drawable.ic_scroll_up),
                tint = Color.Unspecified,
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    SerenityTheme {
        BottomNavigationBar()
    }
}
