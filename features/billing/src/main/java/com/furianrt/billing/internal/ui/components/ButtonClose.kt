package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import com.furianrt.uikit.R as uiR

@Composable
internal fun ButtonClose(
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 32.dp) {
        IconButton(
            modifier = modifier
                .clip(CircleShape)
                .hazeBlur(
                    input = HazeInput.Sources(hazeState),
                    style = HazeBlurStyle {
                        blurRadius(8.dp)
                        noiseFactor(0f)
                        colorEffects(
                            listOf(HazeColorEffect.tint(Color.Transparent))
                        )
                    },
                )
                .systemGestureExclusion(),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = onClick,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_exit),
                contentDescription = null,
            )
        }
    }
}