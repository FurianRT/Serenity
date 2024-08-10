package com.furianrt.settings.internal.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.settings.R
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun Toolbar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
    ) {
        ButtonBack(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
            onClick = onBackClick,
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.settings_screen_title),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@PreviewWithBackground
@Composable
private fun ToolbarPreview() {
    SerenityTheme {
        Toolbar(
            onBackClick = {},
        )
    }
}
