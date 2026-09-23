package com.furianrt.backup.internal.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.backup.R
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

@Composable
internal fun SuccessSyncSnackBar(
    title: String,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_sync_success),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        speed = 1.4f,
    )
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .widthIn(min = 200.dp)
            .clip(RoundedCornerShape(16.dp))
            .hazeBlur(
                input = HazeInput.Sources(hazeState),
                style = HazeBlurStyle {
                    blurRadius(16.dp)
                    colorEffects(
                        listOf(HazeColorEffect.tint(colorScheme.primaryContainer.copy(alpha = 0.3f))),
                    )
                },
            )
            .clickableNoRipple {}
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LottieAnimation(
            modifier = Modifier.size(64.dp),
            composition = composition,
            progress = { lottieState.progress },
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        SuccessSyncSnackBar(
            title = "Successful",
            hazeState = HazeState(),
        )
    }
}
