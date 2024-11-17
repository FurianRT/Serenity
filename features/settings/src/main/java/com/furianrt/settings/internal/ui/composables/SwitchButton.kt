package com.furianrt.settings.internal.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun SwitchButton(
    title: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onCheckedChange)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.applyIf(!enabled) { Modifier.alpha(0.5f) },
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
        Switch(
            modifier = Modifier.padding(start = 8.dp),
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.primary,
            )
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewChecked() {
    SerenityTheme {
        SwitchButton(
            title = "Test title",
            checked = true,
            onCheckedChange = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewUnchecked() {
    SerenityTheme {
        SwitchButton(
            title = "Test title",
            checked = false,
            onCheckedChange = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewDisabled() {
    SerenityTheme {
        SwitchButton(
            title = "Test title",
            checked = false,
            enabled = false,
            onCheckedChange = {},
        )
    }
}
