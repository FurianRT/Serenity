package com.furianrt.uikit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.ToggleOff
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.ToggleOn
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun SwitchWithLabel(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    hint: String? = null,
    enabled: Boolean = true,
    withHaptic: Boolean = true,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (hazeState != null) {
                    Modifier.hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            blurRadius = 12.dp,
                            noiseFactor = 0f,
                            tint = HazeTint(Color.Transparent),
                        ),
                    )
                } else {
                    Modifier
                }
            )
            .clickable(enabled = enabled, onClick = { onCheckedChange(!isChecked) })
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = if (hint == null) {
            Alignment.CenterVertically
        } else {
            Alignment.Top
        },
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .applyIf(!enabled) { Modifier.alpha(0.5f) },
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (hint != null) {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Switch(
            modifier = Modifier.padding(start = 8.dp),
            checked = isChecked,
            onCheckedChange = { checked ->
                val feedbackType = if (checked) ToggleOn else ToggleOff
                if (withHaptic) {
                    hapticFeedback.performHapticFeedback(feedbackType)
                }
                onCheckedChange(checked)
            },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.onTertiaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewChecked() {
    SerenityTheme {
        SwitchWithLabel(
            title = "Test title",
            isChecked = true,
            onCheckedChange = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewUnchecked() {
    SerenityTheme {
        SwitchWithLabel(
            title = "Test title",
            isChecked = false,
            onCheckedChange = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewHint() {
    SerenityTheme {
        SwitchWithLabel(
            title = "Test title",
            hint = "Test hint",
            isChecked = true,
            onCheckedChange = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewDisabled() {
    SerenityTheme {
        SwitchWithLabel(
            title = "Test title",
            isChecked = false,
            enabled = false,
            onCheckedChange = {},
        )
    }
}
