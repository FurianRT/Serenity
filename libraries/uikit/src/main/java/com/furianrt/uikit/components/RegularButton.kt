package com.furianrt.uikit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.uikit.extensions.applyIf

@Composable
fun RegularButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    icon: Painter? = null,
    enabled: Boolean = true,
    elevation: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
) {
    val rippleConfig = RippleConfiguration(
        color = Color.White,
        rippleAlpha = RippleAlpha(
            draggedAlpha = 0.1f,
            focusedAlpha = 0.1f,
            hoveredAlpha = 0.1f,
            pressedAlpha = 0.1f,
        ),
    )
    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig) {
        Button(
            modifier = modifier.applyIf(!enabled) { Modifier.alpha(0.5f) },
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(32.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null
                    )
                }
                BasicText(
                    text = text,
                    style = textStyle,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme::onPrimaryContainer,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 14.sp,
                        maxFontSize = textStyle.fontSize,
                    ),
                )
            }
        }
    }
}