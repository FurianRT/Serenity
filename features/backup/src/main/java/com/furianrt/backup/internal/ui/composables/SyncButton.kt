package com.furianrt.backup.internal.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun SyncButton(
    text: String,
    progress: Float?,
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val progressColor = MaterialTheme.colorScheme.tertiaryContainer
    val alpha by animateFloatAsState(targetValue = if (isEnabled) 1f else 0.5f)
    val progressState by animateFloatAsState(targetValue = progress ?: 0f)
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Button(
            modifier = modifier
                .alpha(alpha)
                .clip(RoundedCornerShape(16.dp))
                .drawWithContent {
                    drawContent()
                    if (progress != null) {
                        drawRect(
                            color = progressColor,
                            size = size.copy(width = size.width * progressState),
                        )
                    }
                }
                .animateContentSize(),
            onClick = onClick,
            enabled = isEnabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = text,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        SyncButton(
            text = "Backup",
            isEnabled = true,
            progress = null,
            onClick = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun PreviewProgress() {
    SerenityTheme {
        SyncButton(
            text = "Backup",
            isEnabled = true,
            progress = 0.5f,
            onClick = {},
        )
    }
}
