package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.uikit.anim.shimmer
import com.furianrt.uikit.components.DotsAnimation
import com.furianrt.uikit.utils.shiftToAccent

@Composable
internal fun ButtonSubscribe(
    text: String,
    showProgress: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rippleConfig = RippleConfiguration(
        color = Color.White,
        rippleAlpha = RippleAlpha(
            draggedAlpha = 0.1f,
            focusedAlpha = 0.1f,
            hoveredAlpha = 0.1f,
            pressedAlpha = 0.1f,
        ),
    )
    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig) {
        Box(
            modifier = modifier
                .heightIn(min = 54.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.shiftToAccent(),
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite,
                    ),
                )
                .shimmer(
                    delayMills = 1500,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                )
                .clickable(enabled = !showProgress, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (showProgress) {
                DotsAnimation()
            } else {
                BasicText(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme::onPrimaryContainer,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 14.sp,
                        maxFontSize = MaterialTheme.typography.titleMedium.fontSize,
                    ),
                )
            }
        }
    }
}
