package com.furianrt.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme

@Composable
fun SnackBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    tonalColor: Color? = null,
    icon: Painter? = null,
) {
    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = color ?: MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .applyIf(tonalColor != null) { Modifier.background(tonalColor!!) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = title,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        SnackBar(
            title = "Test tiles",
            icon = painterResource(R.drawable.ic_error),
            tonalColor = MaterialTheme.colorScheme.tertiary,
        )
    }
}
