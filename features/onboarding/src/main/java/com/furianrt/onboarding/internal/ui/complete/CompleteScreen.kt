package com.furianrt.onboarding.internal.ui.complete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.furianrt.onboarding.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.brighterBy

@Composable
internal fun CompleteScreen(
    modifier: Modifier = Modifier,
) {
    val isInspectionMode = LocalInspectionMode.current
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_onboadring_complete),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        reverseOnRepeat = true,
        speed = 2.3f,
    )

    val leafsColor = MaterialTheme.colorScheme.primaryContainer.brighterBy(0.03f).toArgb()
    val stemColor = MaterialTheme.colorScheme.primaryContainer.brighterBy(0.08f).toArgb()
    val edgeColor = MaterialTheme.colorScheme.primaryContainer.brighterBy(-0.03f).toArgb()

    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = leafsColor,
            keyPath = KeyPath("**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = stemColor,
            keyPath = KeyPath("Long Line", "**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = stemColor,
            keyPath = KeyPath("Left Leaf", "Group 3", "Group 1", "**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = edgeColor,
            keyPath = KeyPath("Left Leaf", "Group 3", "Group 3", "**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = stemColor,
            keyPath = KeyPath("Right Leaf", "Group 2", "Group 1", "**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = edgeColor,
            keyPath = KeyPath("Right Leaf", "Group 2", "Group 3", "**"),
        ),
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieAnimation(
                modifier = Modifier
                    .height(180.dp)
                    .scale(1.4f),
                composition = composition,
                dynamicProperties = dynamicProperties,
                progress = { if (isInspectionMode) 1f else lottieState.progress },
            )
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(R.string.onboarding_complete_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        CompleteScreen()
    }
}
