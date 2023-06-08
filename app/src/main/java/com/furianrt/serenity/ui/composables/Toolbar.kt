package com.furianrt.serenity.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.serenity.R
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.CollapsingToolbarState
import me.onebone.toolbar.ExperimentalToolbarApi

private const val PARALLAX_RATIO = 0.04f

@Composable
fun CollapsingToolbarScope.Toolbar(
    toolbarScaffoldState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
) {
    val toolbarState = toolbarScaffoldState.toolbarState

    val needToSnapParallax by remember {
        derivedStateOf {
            val slippage = 0.005f
            val isCollapsed = toolbarState.progress <= slippage
            val isExpanded = toolbarState.progress >= 1f - slippage
            val isScrolling = toolbarState.isScrollInProgress || listState.isScrollInProgress
            !isScrolling && !isCollapsed && !isExpanded
        }
    }

    LaunchedEffect(needToSnapParallax) {
        if (needToSnapParallax) {
            toolbarState.performSnap()
        }
    }

    Spacer(
        modifier = Modifier
            .pin()
            .fillMaxWidth()
            .height(0.5.dp)
    )

    Row(
        modifier = Modifier
            .parallax(ratio = PARALLAX_RATIO)
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 32.dp)
            .graphicsLayer {
                val scale = 1f - PARALLAX_RATIO * (1f - toolbarState.progress)
                scaleX = scale
                scaleY = scale
                alpha = toolbarState.progress
            },
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

@OptIn(ExperimentalToolbarApi::class)
private suspend fun CollapsingToolbarState.performSnap(duration: Int = 350) {
    val center = 0.5f
    if (progress > center && progress < 1f) {
        expand(duration)
    } else if (progress <= center && progress > 0f) {
        collapse(duration)
    }
}
