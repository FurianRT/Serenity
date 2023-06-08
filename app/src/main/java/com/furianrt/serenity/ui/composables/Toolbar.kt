package com.furianrt.serenity.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.serenity.R
import com.furianrt.uikit.extensions.isCollapsed
import com.furianrt.uikit.extensions.isExpanded
import com.furianrt.uikit.extensions.performSnap
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope

private const val PARALLAX_RATIO = 0.04f
private const val ALPHA_ANIM_SPEED_MODIFIER = 0.65f

@Composable
fun CollapsingToolbarScope.Toolbar(
    toolbarScaffoldState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
) {
    val toolbarState = toolbarScaffoldState.toolbarState

    val needToSnapParallax by remember {
        derivedStateOf {
            val isScrolling = toolbarState.isScrollInProgress || listState.isScrollInProgress
            !isScrolling && !toolbarState.isCollapsed && !toolbarState.isExpanded
        }
    }

    LaunchedEffect(needToSnapParallax) {
        if (needToSnapParallax) {
            toolbarState.performSnap()
        }
    }

    val needToSnapPin by remember {
        derivedStateOf {
            val isScrolling = toolbarState.isScrollInProgress || listState.isScrollInProgress
            !isScrolling && !toolbarScaffoldState.isCollapsed && !toolbarScaffoldState.isExpanded
        }
    }

    LaunchedEffect(needToSnapPin) {
        if (needToSnapPin) {
            toolbarScaffoldState.performSnap()
        }
    }

    val toolbarHeightDp = 64.dp

    BotHint(
        modifier = Modifier
            .parallax(PARALLAX_RATIO)
            .padding(top = toolbarHeightDp)
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp)
            .graphicsLayer {
                translationY = -size.height
                alpha = 1f - (1f - toolbarState.progress) / ALPHA_ANIM_SPEED_MODIFIER
                val scale = 1f - PARALLAX_RATIO * (1f - toolbarState.progress)
                scaleX = scale
                scaleY = scale
            },
    )

    Box(
        modifier = Modifier
            .pin()
            .height(toolbarHeightDp)
            .graphicsLayer {
                val parallaxHeight = toolbarState.maxHeight - toolbarState.minHeight
                val scrolledHeight = parallaxHeight.toFloat() - toolbarState.height + size.height
                translationY = parallaxHeight.toFloat() - scrolledHeight
            },
        contentAlignment = Alignment.Center,
    ) {
        Search(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp),
        )
    }
}

@Composable
private fun Search(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            modifier = Modifier
                .height(36.dp)
                .padding(end = 12.dp)
                .weight(1f),
        )
        Box(
            modifier = Modifier
                .clickable(
                    onClick = {},
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        color = MaterialTheme.colorScheme.onSecondary,
                        bounded = false,
                    ),
                )
                .padding(4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun BotHint(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.ai_murble_anim),
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )

        LottieAnimation(
            modifier = Modifier.size(48.dp),
            composition = composition,
            progress = { progress },
        )

        ChatMessage(
            modifier = Modifier
                .padding(start = 10.dp),
        )
    }
}
