package com.furianrt.uikit.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun GeneralButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    iconPainter: Painter? = null,
    hint: String? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .applyIf(!enabled) { Modifier.alpha(0.5f) }
            .padding(start = 8.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = if (hint != null) Alignment.Top else Alignment.CenterVertically
    ) {
        if (iconPainter != null) {
            Icon(
                modifier = Modifier.padding(start = 4.dp),
                painter = iconPainter,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .then(
                        if (hazeState != null) {
                            Modifier.hazeEffect(
                                state = hazeState,
                                style = HazeDefaults.style(
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    blurRadius = 16.dp,
                                    noiseFactor = 0f,
                                    tint = HazeTint(Color.Transparent),
                                ),
                            )
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 4.dp),
                text = title,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (hint != null) {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .then(
                            if (hazeState != null) {
                                Modifier.hazeEffect(
                                    state = hazeState,
                                    style = HazeDefaults.style(
                                        backgroundColor = MaterialTheme.colorScheme.surface,
                                        blurRadius = 16.dp,
                                        noiseFactor = 0f,
                                        tint = HazeTint(Color.Transparent),
                                    ),
                                )
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 4.dp)
                        .alpha(0.5f),
                    text = hint,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        GeneralButton(
            title = "test title",
            iconPainter = painterResource(R.drawable.ic_error),
            hint = "Test hint",
            onClick = {},
        )
    }
}
