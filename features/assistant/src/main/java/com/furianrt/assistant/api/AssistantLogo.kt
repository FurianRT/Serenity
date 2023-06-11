package com.furianrt.assistant.api

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.assistant.R
import kotlinx.coroutines.launch

private const val ANIM_SCALE_DURATION = 300

@Composable
fun AssistantLogo(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_ai_murble),
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        modifier = modifier
            .clickable(
                onClick = {
                    if (scale.isRunning) {
                        return@clickable
                    }
                    scope.launch {
                        scale.animateTo(
                            targetValue = 1.1f,
                            animationSpec = tween(ANIM_SCALE_DURATION / 2)
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(ANIM_SCALE_DURATION / 2)
                        )
                    }
                    onClick()
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
}