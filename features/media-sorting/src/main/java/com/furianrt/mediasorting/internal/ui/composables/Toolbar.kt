package com.furianrt.mediasorting.internal.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.mediasorting.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun Toolbar(
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
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
            text = stringResource(R.string.media_sorting_title),
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .systemGestureExclusion(),
            onClick = onDoneClick,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_action_done),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        Toolbar(
            onBackClick = {},
            onDoneClick = {},
        )
    }
}
