package com.furianrt.mediasorting.internal.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import com.furianrt.uikit.R as uiR

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun AddMediaButton(
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
) {
    Box(
        modifier = modifier
            .padding(start = 8.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
            .clip(shape)
            .hazeGlass(
                input = HazeInput.Sources(hazeState),
                style = LocalGlassStyle.current.then {
                    contrast(0f)
                    whitePoint(0f)
                    shape(shape)
                },
                performanceMode = HazePerformanceMode.Performance,
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(onClick = onClick)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_add_media_big),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceContainerLow,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        AddMediaButton(
            hazeState = HazeState(),
            onClick = {},
        )
    }
}
