package com.furianrt.assistant.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.assistant.R
import com.furianrt.uikit.extensions.clickableWithScaleAnim

@Composable
fun AssistantLogo(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_ai_murble),
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        modifier = modifier.clickableWithScaleAnim(
            duration = AssistantDefaults.ANIM_LOGO_CLICK_SCALE_DURATION,
            onClick = onClick,
        ),
        composition = composition,
        progress = { progress },
    )
}
