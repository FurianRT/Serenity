package com.furianrt.uikit.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

@Composable
fun OptionButtonWrapper(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderColor: Color = MaterialTheme.colorScheme.background,
    backgroundColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(shape)
            .hazeBlur(
                input = HazeInput.Sources(hazeState),
                style = HazeBlurStyle {
                    blurRadius(6.dp)
                    noiseFactor(0.1f)
                    colorEffects(listOf(HazeColorEffect.tint(backgroundColor)))
                },
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape,
            ),
        content = { content() },
    )
}