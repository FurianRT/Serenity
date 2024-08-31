package com.furianrt.mediaselector.internal.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun SelectedCountHint(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clickableNoRipple {}
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        SelectedCountHint(
            title = "Attach 3 media",
        )
    }
}
