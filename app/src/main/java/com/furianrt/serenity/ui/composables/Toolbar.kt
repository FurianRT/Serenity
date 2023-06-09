package com.furianrt.serenity.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.serenity.R
import com.furianrt.uikit.extensions.isInMiddleState
import com.furianrt.uikit.extensions.performSnap
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope

private const val PARALLAX_RATIO = 0.03f
private const val ANIM_BUTTON_AI_DURATION = 300
private const val ANIM_BUTTON_SETTINGS_DURATION = 250
private const val ANIM_BUTTON_SETTINGS_ROTATION = 60f

@Composable
fun CollapsingToolbarScope.Toolbar(
    toolbarScaffoldState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
    onSettingsClick: () -> Unit,
) {
    val toolbarState = toolbarScaffoldState.toolbarState

    val needToSnapParallax by remember {
        derivedStateOf {
            val isScrolling = toolbarState.isScrollInProgress || listState.isScrollInProgress
            !isScrolling && toolbarState.isInMiddleState
        }
    }

    val needToSnapPin by remember {
        derivedStateOf {
            val isScrolling = toolbarState.isScrollInProgress || listState.isScrollInProgress
            !isScrolling && toolbarScaffoldState.isInMiddleState
        }
    }

    LaunchedEffect(needToSnapParallax, needToSnapPin) {
        if (needToSnapParallax) {
            toolbarState.performSnap()
        }

        if (needToSnapPin) {
            toolbarScaffoldState.performSnap()
        }
    }

    val toolbarHeightDp = 64.dp
    var botHintTop by remember { mutableStateOf(0f) }
    var searchBarTop by remember { mutableStateOf(0f) }

    BotHint(
        modifier = Modifier
            .parallax(PARALLAX_RATIO)
            .padding(top = toolbarHeightDp)
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
            .onGloballyPositioned { botHintTop = it.boundsInRoot().top }
            .drawWithContent {
                clipRect(
                    right = size.width,
                    top = -size.height,
                    bottom = if (botHintTop == 0f) -size.height else searchBarTop - botHintTop,
                ) {
                    this@drawWithContent.drawContent()
                }
            }
            .graphicsLayer {
                val scale = 1f - PARALLAX_RATIO * (1f - toolbarState.progress)
                scaleX = scale
                scaleY = scale
                alpha = toolbarState.progress
                translationY = -size.height
            },
    )

    Row(
        modifier = Modifier
            .pin()
            .height(toolbarHeightDp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .graphicsLayer { translationY = toolbarState.height - size.height },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            modifier = Modifier
                .height(36.dp)
                .padding(end = 16.dp)
                .weight(1f)
                .onGloballyPositioned { searchBarTop = it.boundsInRoot().top },
        )
        SettingsButton(onClick = onSettingsClick)
    }
}

@Composable
private fun SettingsButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    var isAnimStarted by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isAnimStarted) {
        if (isAnimStarted) {
            joinAll(
                launch {
                    scale.animateTo(
                        targetValue = 1.1f,
                        animationSpec = tween(ANIM_BUTTON_SETTINGS_DURATION / 2)
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(ANIM_BUTTON_SETTINGS_DURATION / 2)
                    )
                },
                launch {
                    rotation.animateTo(
                        targetValue = rotation.value + ANIM_BUTTON_SETTINGS_ROTATION,
                        animationSpec = tween(ANIM_BUTTON_SETTINGS_DURATION)
                    )
                }
            )
            isAnimStarted = false
        }
    }

    Icon(
        modifier = modifier
            .clickable(
                onClick = {
                    isAnimStarted = true
                    onClick()
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            )
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = rotation.value
            },
        painter = painterResource(R.drawable.ic_settings),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
private fun BotHint(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var isAnimStarted by remember { mutableStateOf(false) }
        val scale = remember { Animatable(1f) }

        LaunchedEffect(isAnimStarted) {
            if (isAnimStarted) {
                scale.animateTo(
                    targetValue = 1.1f,
                    animationSpec = tween(ANIM_BUTTON_AI_DURATION / 2)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(ANIM_BUTTON_AI_DURATION / 2)
                )
                isAnimStarted = false
            }
        }
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.anim_ai_murble),
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )

        LottieAnimation(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    onClick = {
                        isAnimStarted = true
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                )
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            composition = composition,
            progress = { progress },
        )
        ChatMessage(modifier = Modifier.padding(start = 10.dp))
    }
}
