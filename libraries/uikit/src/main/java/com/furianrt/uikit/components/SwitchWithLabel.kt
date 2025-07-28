package com.furianrt.uikit.components

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
fun SwitchWithLabel(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = { onCheckedChange(!isChecked) })
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .applyIf(!enabled) { Modifier.alpha(0.5f) },
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
        Switch(
            modifier = Modifier.padding(start = 8.dp),
            checked = isChecked,
            onCheckedChange = onCheckedChange,
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
