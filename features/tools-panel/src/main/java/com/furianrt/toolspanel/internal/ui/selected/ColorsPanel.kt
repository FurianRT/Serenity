package com.furianrt.toolspanel.internal.ui.selected

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.toolspanel.internal.ui.common.ColorItem
import com.furianrt.uikit.extensions.drawLeftShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.furianrt.uikit.R as uiR

@Composable
internal fun ColorsPanel(
    colors: ImmutableList<UiNoteFontColor>,
    selectedColor: UiNoteFontColor?,
    onColorSelected: (color: UiNoteFontColor?) -> Unit = {},
    onCloseClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(count = colors.count()) { index ->
                ColorItem(
                    modifier = Modifier.size(32.dp),
                    color = colors[index],
                    isSelected = { it == selectedColor },
                    onClick = { color -> onColorSelected(color.takeIf { selectedColor != it }) },
                )
            }
        }
        IconButton(
            modifier = Modifier.drawBehind {
                if (listState.canScrollForward) {
                    drawLeftShadow(elevation = 2.dp)
                }
            },
            onClick = onCloseClick,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_exit),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ColorsPanel(
            colors = UiNoteFontColor.entries.toImmutableList(),
            selectedColor = UiNoteFontColor.GREY_DARK,
        )
    }
}
