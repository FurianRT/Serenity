package com.furianrt.uikit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
fun RadioButtonWithText(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontFamily: FontFamily? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Text(
            text = title,
            style = textStyle,
            fontFamily = fontFamily,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
@PreviewWithBackground
private fun SelectedPreview() {
    SerenityTheme {
        RadioButtonWithText(
            title = "Test title",
            isSelected = true,
            onClick = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun UnselectedPreview() {
    SerenityTheme {
        RadioButtonWithText(
            title = "Test title",
            isSelected = false,
            onClick = {},
        )
    }
}
