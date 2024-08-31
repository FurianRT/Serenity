package com.furianrt.serenity.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.serenity.R
import com.furianrt.uikit.components.ActionButton
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.launch

private const val ANIM_OFFSET_DURATION = 500
private const val ANIM_BUTTON_SCROLL_DURATION = 350
private const val LABEL_OFFSET_ANIM = "BottomNavigationBar_offset_anim"

@Composable
internal fun BottomNavigationBar(
    needToHideNavigation: () -> Boolean,
    needToShowScrollUpButton: () -> Boolean,
    onScrollToTopClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val verticalBias by animateFloatAsState(
        targetValue = if (needToHideNavigation()) 1f else 0f,
        animationSpec = tween(durationMillis = ANIM_OFFSET_DURATION),
        label = LABEL_OFFSET_ANIM,
    )

    val navBarsHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(contentPadding)
            .fillMaxWidth()
            .graphicsLayer {
                val bottomPadding = contentPadding
                    .calculateBottomPadding()
                    .toPx()
                translationY = (size.height + bottomPadding + navBarsHeight.toPx()) * verticalBias
            },
        contentAlignment = Alignment.Center,
    ) {
        ButtonScrollToTop(
            isVisible = needToShowScrollUpButton,
            onClick = onScrollToTopClick,
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ActionButton(
                icon = painterResource(R.drawable.ic_fab_add),
                onClick = onAddNoteClick,
            )
        }
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
    var buttonHeight by remember { mutableIntStateOf(0) }

    AnimatedVisibility(
        modifier = Modifier
            .graphicsLayer { translationY = translation.value }
            .onGloballyPositioned { buttonHeight = it.size.height },
        visible = isVisible(),
        enter = fadeIn(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
        exit = fadeOut(animationSpec = tween(durationMillis = ANIM_BUTTON_SCROLL_DURATION)),
    ) {
        ElevatedButton(
            modifier = modifier.heightIn(min = 32.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = {
                if (translation.isRunning) {
                    return@ElevatedButton
                }
                scope.launch {
                    translation.animateTo(
                        targetValue = buttonHeight.toFloat(),
                        animationSpec = tween(ANIM_BUTTON_SCROLL_DURATION),
                    )
                    translation.snapTo(0f)
                }
                onClick()
            },
        ) {
            Text(
                text = stringResource(id = R.string.button_scroll_to_top_title),
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
