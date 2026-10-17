package com.furianrt.toolspanel.internal.ui.selected

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.toolspanel.api.ToolsPanelConstants
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.toolspanel.internal.ui.common.ButtonKeyboard
import com.furianrt.toolspanel.internal.ui.common.ColorItem
import com.furianrt.toolspanel.internal.ui.common.CustomColorItem
import com.furianrt.uikit.extensions.drawLeftShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun ColorsPanel(
    colors: List<Color>,
    selectedColor: Color?,
    modifier: Modifier = Modifier,
    showKeyboardButton: Boolean,
    onColorSelected: (color: Color?) -> Unit = {},
    onCustomColorClick: () -> Unit = {},
    onKeyboardClickClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    val rotation by animateFloatAsState(
        targetValue = if (showKeyboardButton) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
    )

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            item {
                Crossfade(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .graphicsLayer { rotationZ = rotation },
                    targetState = showKeyboardButton,
                ) { targetState ->
                    if (targetState) {
                        ButtonKeyboard(
                            modifier = Modifier.size(30.dp),
                            onClick = onKeyboardClickClick,
                        )
                    } else {
                        CustomColorItem(
                            modifier = Modifier.size(30.dp),
                            onClick = onCustomColorClick,
                        )
                    }
                }
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
        ButtonClose(
            modifier = Modifier.drawBehind {
                if (listState.canScrollForward) {
                    drawLeftShadow(color = shadowColor)
                }
            },
            painter = painterResource(uiR.drawable.ic_action_done),
            onClick = onCloseClick,
        )
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
            showKeyboardButton = false,
        )
    }
}
