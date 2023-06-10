package com.furianrt.serenity.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.serenity.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.launch

private const val ANIM_OFFSET_DURATION = 350
private const val ANIM_BUTTON_SCROLL_DURATION = 350
private const val ANIM_BUTTON_ADD_DURATION = 150
private const val LABEL_OFFSET_ANIM = "BottomNavigationBar_offset_anim"

@Composable
fun BottomNavigationBar(
    needToHideNavigation: () -> Boolean,
    needToShowScrollUpButton: () -> Boolean,
    onScrollToTopClick: () -> Unit,
    onAddNoteClick: () -> Unit,
) {
    val verticalBias by animateFloatAsState(
        targetValue = if (needToHideNavigation()) 1f else 0f,
        animationSpec = tween(durationMillis = ANIM_OFFSET_DURATION),
        label = LABEL_OFFSET_ANIM,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = size.height * verticalBias },
        contentAlignment = Alignment.Center,
    ) {
        ButtonScrollToTop(
            modifier = Modifier.padding(bottom = 24.dp),
            isVisible = needToShowScrollUpButton,
            onClick = onScrollToTopClick,
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ButtonAddNote(
                modifier = Modifier.padding(end = 24.dp, bottom = 24.dp),
                onClick = onAddNoteClick,
            )
        }
    }
}

@Composable
private fun ButtonAddNote(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    FloatingActionButton(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.secondary,
        onClick = {
            if (scale.isRunning) {
                return@FloatingActionButton
            }
            scope.launch {
                scale.animateTo(
                    targetValue = 1.05f,
                    animationSpec = tween(ANIM_BUTTON_ADD_DURATION / 2)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(ANIM_BUTTON_ADD_DURATION / 2)
                )
            }
            onClick()
        },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null,
        )
    }
}

@Composable
private fun ButtonScrollToTop(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val translation = remember { Animatable(0f) }
    var buttonHeight by remember { mutableStateOf(0) }

    AnimatedVisibility(
        modifier = Modifier
            .graphicsLayer { translationY = translation.value }
            .onGloballyPositioned { buttonHeight = it.size.height },
        visible = isVisible(),
        enter = fadeIn(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
        exit = fadeOut(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
    ) {
        Box(
            modifier = modifier
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(24.dp),
                )
                .clickable(
                    onClick = {
                        if (translation.isRunning) {
                            return@clickable
                        }
                        scope.launch {
                            translation.animateTo(
                                targetValue = buttonHeight.toFloat(),
                                animationSpec = tween(ANIM_BUTTON_SCROLL_DURATION)
                            )
                            translation.snapTo(0f)
                        }
                        onClick()
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        color = MaterialTheme.colorScheme.onSecondary,
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                text = stringResource(id = uiR.string.button_scroll_to_top_title),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    SerenityTheme {
        BottomNavigationBar(
            needToHideNavigation = { false },
            needToShowScrollUpButton = { true },
            onScrollToTopClick = {},
            onAddNoteClick = {},
        )
    }
}
