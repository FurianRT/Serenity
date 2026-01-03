package com.furianrt.onboarding.internal.ui.complete

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.onboarding.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

@Composable
internal fun CompleteScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_onboarding_done),
    )
    val animatable = rememberLottieAnimatable()

    LaunchedEffect(Unit) {
        delay(400)
        launch { animatable.animate(composition = composition) }
        if (vibrator.hasVibrator()) {
            delay(100)
            val pattern = longArrayOf(0, 20, 15, 40, 20, 30, 15, 25, 15, 20, 20, 30, 25, 15)
            val amplitudes = intArrayOf(0, 70, 0, 230, 0, 150, 0, 120, 0, 90, 0, 110, 0, 60)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.scale(2.2f),
                painter = painterResource(uiR.mipmap.ic_launcher_foreground),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(R.string.onboarding_complete_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }
        LottieAnimation(
            modifier = Modifier.scale(2.5f),
            composition = composition,
            progress = { animatable.progress },
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        CompleteScreen()
    }
}
