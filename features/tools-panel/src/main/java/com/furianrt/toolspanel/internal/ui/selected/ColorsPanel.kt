package com.furianrt.toolspanel.internal.ui.selected

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.furianrt.core.mapImmutable
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.toolspanel.internal.ui.common.ColorItem
import com.furianrt.uikit.extensions.drawLeftShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun ColorsPanel(
    colors: ImmutableList<Color>,
    selectedColor: Color?,
    modifier: Modifier = Modifier,
    onColorSelected: (color: Color?) -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
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
            itemsIndexed(items = colors) { _, item ->
                ColorItem(
                    modifier = Modifier.size(32.dp),
                    color = item,
                    isSelected = item == selectedColor,
                    onClick = { color -> onColorSelected(color.takeIf { selectedColor != it }) },
                )
            }
        }
        ButtonClose(
            modifier = Modifier.drawBehind {
                if (listState.canScrollForward) {
                    drawLeftShadow(color = shadowColor)
                }
            },
            onClick = onCloseClick,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ColorsPanel(
            colors = UiNoteFontColor.entries.mapImmutable { it.value },
            selectedColor = UiNoteFontColor.BLUE_DARK.value,
        )
    }
}
