package com.furianrt.uikit.components

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode

private const val ANIM_DURATION = 300
private const val INITIAL_SALE = 0.2f

@Composable
fun defaultMenuItemColors() = MenuItemColors(
    textColor = MaterialTheme.colorScheme.onSurface,
    leadingIconColor = MaterialTheme.colorScheme.onSurface,
    trailingIconColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
)

@Composable
fun MenuItem(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: MenuItemColors = defaultMenuItemColors(),
    animate: Boolean = true,
) {
    val isInspectionMode = LocalInspectionMode.current
    val scale = remember { Animatable(if (animate) INITIAL_SALE else 1f) }
    val interpolator = remember { OvershootInterpolator() }
    if (animate) {
        LaunchedEffect(Unit) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ANIM_DURATION,
                    easing = { interpolator.getInterpolation(it) },
                ),
            )
        }
    }
    DropdownMenuItem(
        modifier = modifier,
        text = text,
        colors = colors,
        leadingIcon = if (leadingIcon != null) {
            {
                Box(
                    modifier = Modifier.graphicsLayer {
                        scaleY = if (isInspectionMode) 1f else scale.value
                    },
                ) {
                    leadingIcon()
                }
            }
        } else {
            null
        },
        onClick = onClick,
    )
}

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    colors: MenuItemColors = defaultMenuItemColors(),
) {
    MenuItem(
        modifier = modifier,
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
            )
        },
        colors = colors,
        leadingIcon = if (icon != null) {
            {
                Icon(
                    painter = icon,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        onClick = onClick,
    )
}