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
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun OptionButtonWrapper(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 16.dp,
                    tint = HazeTint(Color.Transparent),
                ),
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp),
            ),
        content = { content() },
    )
}