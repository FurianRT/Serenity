package com.furianrt.toolspanel.internal.ui.selected

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.toolspanel.api.ToolsPanelConstants
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.toolspanel.internal.ui.common.ColorItem
import com.furianrt.toolspanel.internal.ui.common.CustomColorItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun ColorsPanel(
    colors: List<Color>,
    selectedColor: Color?,
    modifier: Modifier = Modifier,
    onColorSelected: (color: Color?) -> Unit = {},
    onCustomColorClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    LazyRow(
        modifier = modifier.fillMaxSize(),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        item {
            ButtonClose(
                modifier = Modifier
                    .padding(end = 2.dp)
                    .size(30.dp),
                painter = painterResource(uiR.drawable.ic_arrow_back),
                onClick = onCloseClick,
            )
        }
        item {
            CustomColorItem(
                modifier = Modifier.size(30.dp),
                onClick = onCustomColorClick,
            )
        }
        itemsIndexed(items = colors) { _, item ->
            ColorItem(
                modifier = Modifier.size(32.dp),
                color = item,
                isSelected = item == selectedColor,
                onClick = { color -> onColorSelected(color.takeIf { selectedColor != it }) },
            )
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ColorsPanel(
            modifier = Modifier.height(ToolsPanelConstants.PANEL_HEIGHT),
            colors = UiNoteFontColor.entries.map { it.value },
            selectedColor = UiNoteFontColor.BLUE_DARK.value,
        )
    }
}
