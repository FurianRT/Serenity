package com.furianrt.mediasorting.internal.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.mediasorting.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun DragAndDropHint(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .padding(24.dp)
            .alpha(0.5f),
        text = stringResource(R.string.media_sorting_drag_and_drop_hint),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        DragAndDropHint()
    }
}
