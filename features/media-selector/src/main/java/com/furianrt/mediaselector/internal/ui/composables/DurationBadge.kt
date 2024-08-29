package com.furianrt.mediaselector.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun DurationBadge(
    duration: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = duration,
            fontSize = 12.sp,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        DurationBadge(duration = "10:35")
    }
}
