package com.furianrt.onboarding.internal.ui.greeting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.furianrt.onboarding.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.brighterBy

@Composable
internal fun GreetingScreen(
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_onboadring_greeting),
    )

    val animatable = rememberLottieAnimatable()

    val mainColor = MaterialTheme.colorScheme.primaryContainer.brighterBy(0.05f)

    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = mainColor.toArgb(),
            keyPath = KeyPath("**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.GRADIENT_COLOR,
            value = arrayOf(
                mainColor.toArgb(),
                mainColor.brighterBy(0.05f).toArgb(),
                mainColor.brighterBy(0.2f).toArgb(),
            ),
            keyPath = KeyPath(
                "Top Flower",
                "Lotus Icon w BG",
                "Lotus Animated",
                "Lotus",
                "Bulb Parts",
                "Lines",
                "**",
            ),
        ),
        LottieDynamicProperty(
            property = LottieProperty.GRADIENT_COLOR,
            value = arrayOf(
                mainColor.toArgb(),
                mainColor.brighterBy(0.05f).toArgb(),
                mainColor.brighterBy(0.1f).toArgb(),
            ),
            keyPath = KeyPath(
                "Top Flower",
                "Lotus Icon w BG",
                "Lotus Animated",
                "Lotus",
                "Bulb Parts",
                "bulb",
                "**",
            ),
        ),
    )

    LaunchedEffect(composition) {
        animatable.animate(
            composition = composition,
            initialProgress = 0.5f,
            iterations = LottieConstants.IterateForever
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LottieAnimation(
            modifier = Modifier
                .height(150.dp)
                .scale(2.5f),
            composition = composition,
            dynamicProperties = dynamicProperties,
            progress = { animatable.progress },
        )
        Spacer(Modifier.height(24.dp))
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.onboarding_greeting_page_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.onboarding_greeting_page_body),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        GreetingScreen()
    }
}
