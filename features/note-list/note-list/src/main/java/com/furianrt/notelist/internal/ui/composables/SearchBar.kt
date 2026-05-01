package com.furianrt.notelist.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.notelist.R
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun SearchBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                    noiseFactor = 0f,
                    blurRadius = 8.dp,
                )
            )
            .background(MaterialTheme.colorScheme.background)
            .clickableNoRipple(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier
                .alpha(0.6f)
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.notes_list_search_bar_title),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    SerenityTheme {
        SearchBar(
            hazeState = rememberHazeState(),
            onClick = {},
        )
    }
}
