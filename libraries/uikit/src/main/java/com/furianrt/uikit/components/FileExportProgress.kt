package com.furianrt.uikit.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
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
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.clickableNoRipple

@Composable
fun FileExportProgress(
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_file_export),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        reverseOnRepeat = true,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        BackHandler(
            enabled = true,
            onBack = {},
        )
        LottieAnimation(
            modifier = Modifier
                .size(150.dp),
            composition = composition,
            progress = { lottieState.progress },
            dynamicProperties = dynamicProperties,
        )
    }
}