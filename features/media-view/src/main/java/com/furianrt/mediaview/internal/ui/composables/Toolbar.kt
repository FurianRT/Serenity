package com.furianrt.mediaview.internal.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.components.ButtonMenu
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState
import com.furianrt.uikit.R as uiR

internal class ToolbarState {
    var showDropDownMenu by mutableStateOf(false)
}

@Composable
internal fun Toolbar(
    state: ToolbarState,
    totalImages: Int,
    currentImageIndex: Int,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSaveMediaClick: () -> Unit = {},
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
            onClick = onBackClick,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Counter(
            modifier = Modifier.weight(1f),
            total = totalImages,
            currentIndex = currentImageIndex,
        )
        Box {
            ButtonMenu(
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { state.showDropDownMenu = true },
            )
            Menu(
                expanded = state.showDropDownMenu,
                hazeState = hazeState,
                onDeleteClick = onDeleteClick,
                onSaveMediaClick = onSaveMediaClick,
                onShareClick = onShareClick,
                onDismissRequest = { state.showDropDownMenu = false },
            )
        }
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
            state = ToolbarState(),
            totalImages = 50,
            currentImageIndex = 25,
            hazeState = HazeState(),
        )
    }
}
