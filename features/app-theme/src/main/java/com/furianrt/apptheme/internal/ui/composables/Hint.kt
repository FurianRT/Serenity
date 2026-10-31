package com.furianrt.apptheme.internal.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.furianrt.apptheme.R
import dev.chrisbanes.haze.ExperimentalHazeApi

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun Hint(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.app_theme_remove_background_hint),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
    )
}