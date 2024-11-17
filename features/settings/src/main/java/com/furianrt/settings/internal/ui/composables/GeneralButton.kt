package com.furianrt.settings.internal.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.settings.R
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun GeneralButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconPainter: Painter? = null,
    hint: String? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .applyIf(!enabled) { Modifier.alpha(0.5f) }
            .padding(12.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (iconPainter != null) {
            Icon(
                painter = iconPainter,
                contentDescription = title,
                tint = Color.Unspecified,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (hint != null) {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = hint,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        GeneralButton(
            title = "test title",
            iconPainter = painterResource(R.drawable.ic_info),
            hint = "Test hint",
            onClick = {},
        )
    }
}
