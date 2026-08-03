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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import com.furianrt.uikit.R as uiR

@Composable
internal fun AddMediaButton(
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(start = 8.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 6.dp,
                    noiseFactor = 0f,
                    tint = HazeTint(Color.Transparent),
                ),
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(onClick = onClick)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_add_media_big),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiaryContainer,
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
