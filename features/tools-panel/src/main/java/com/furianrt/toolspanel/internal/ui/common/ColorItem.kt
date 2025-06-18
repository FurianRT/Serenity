package com.furianrt.toolspanel.internal.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: (color: Color?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .applyIf(isSelected) {
                Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    .padding(2.dp)
            }
            .background(color, RoundedCornerShape(16.dp))
            .clickableNoRipple { if (isSelected) onClick(null) else onClick(color) },
    )
}

@Preview(widthDp = 40, heightDp = 40)
@Composable
private fun PreviewSelected() {
    SerenityTheme {
        ColorItem(
            color = UiNoteFontColor.GREEN.value,
            isSelected = true,
            onClick = {},
        )
    }
}

@Preview(widthDp = 40, heightDp = 40)
@Composable
private fun PreviewUnselected() {
    SerenityTheme {
        ColorItem(
            color = UiNoteFontColor.GREEN.value,
            isSelected = false,
            onClick = {},
        )
    }
}
