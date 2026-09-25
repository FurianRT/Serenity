package com.furianrt.settings.internal.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.settings.R
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.hazeGlass

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun Version(
    name: String,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .hazeGlass(
                input = HazeInput.Sources(hazeState),
                style = LocalGlassStyle.current.then {
                    whitePoint(0f)
                    contrast(0f)
                },
                performanceMode = HazePerformanceMode.Performance,
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
        text = stringResource(R.string.settings_version_title, name),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    )
}
