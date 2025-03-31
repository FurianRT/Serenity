package com.furianrt.uikit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
fun DefaultToolbar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ToolbarConstants.toolbarHeight),
    ) {
        ButtonBack(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
                .systemGestureExclusion(),
            onClick = onBackClick,
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        DefaultToolbar(
            title = "Test title",
            onBackClick = {},
        )
    }
}
