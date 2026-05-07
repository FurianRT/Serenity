package com.furianrt.reminders.internal.ui.list.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.reminders.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import com.furianrt.uikit.R as uiR

@Composable
internal fun TroubleshootingBlock(
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            blurRadius = 16.dp,
                            noiseFactor = 0f,
                            tint = HazeTint(Color.Transparent),
                        ),
                    )
                    .padding(horizontal = 6.dp),
                text = stringResource(R.string.reminders_troubleshooting_title),
                style = MaterialTheme.typography.bodyMedium,
            )
            Icon(
                modifier = Modifier.alpha(0.5f),
                painter = painterResource(uiR.drawable.ic_error_outline),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 16.dp,
                        noiseFactor = 0f,
                        tint = HazeTint(Color.Transparent),
                    ),
                ),
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .alpha(0.5f),
                text = stringResource(R.string.reminders_troubleshooting_body),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        TroubleshootingBlock(
            hazeState = rememberHazeState(),
            onClick = {},
        )
    }
}
