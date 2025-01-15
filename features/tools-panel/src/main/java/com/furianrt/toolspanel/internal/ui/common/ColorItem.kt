package com.furianrt.toolspanel.internal.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple

@Composable
internal fun ColorItem(
    color: UiNoteFontColor,
    isSelected: (color: UiNoteFontColor) -> Boolean,
    onClick: (color: UiNoteFontColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color.value, CircleShape)
            .applyIf(isSelected(color)) {
                Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
            }
            .clickableNoRipple { onClick(color) },
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected(color)) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(R.drawable.ic_action_done),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}