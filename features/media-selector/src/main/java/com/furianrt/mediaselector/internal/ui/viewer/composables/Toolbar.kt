package com.furianrt.mediaselector.internal.ui.viewer.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.mediaselector.internal.ui.selector.composables.CheckBox
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.R as uiR

@Composable
internal fun Toolbar(
    totalImages: Int,
    currentImageIndex: Int,
    selectionState: SelectionState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ToolbarConstants.toolbarHeight)
            .clickableNoRipple {}
            .padding(horizontal = 4.dp)
            .systemGestureExclusion(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ButtonBack(
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            onClick = onBackClick,
        )
        Counter(
            modifier = Modifier.weight(1f),
            total = totalImages,
            currentIndex = currentImageIndex,
        )
        CheckBox(
            modifier = Modifier.padding(end = 16.dp),
            state = selectionState,
            size = 28.dp,
            onClick = onSelectClick,
        )
    }
}

@Composable
private fun Counter(
    total: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        textAlign = TextAlign.Center,
        text = stringResource(uiR.string.media_counter_pattern, currentIndex + 1, total),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}


@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        Toolbar(
            totalImages = 50,
            currentImageIndex = 25,
            selectionState = SelectionState.Selected(1),
        )
    }
}
