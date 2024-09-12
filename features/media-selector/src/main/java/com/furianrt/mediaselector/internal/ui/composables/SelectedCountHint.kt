package com.furianrt.mediaselector.internal.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.furianrt.mediaselector.R
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlin.math.max

@Composable
internal fun SelectedCountHint(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.media_selector_apply_button_title, max(1, count)),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        SelectedCountHint(
            count = 3,
        )
    }
}
