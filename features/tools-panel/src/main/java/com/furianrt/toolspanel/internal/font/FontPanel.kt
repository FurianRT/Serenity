package com.furianrt.toolspanel.internal.font

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.toImmutableList
import java.util.Locale
import kotlin.math.max
import com.furianrt.uikit.R as uiR

private var cachedImeHeight = 300.dp
private const val FONT_CONTENT_TAG = "font_content"

@Composable
internal fun FontTitleBar(
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.font_panel_title),
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(
            modifier = Modifier
                .padding(end = 4.dp)
                .align(Alignment.CenterEnd),
            onClick = onDoneClick,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_exit),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FontContent(
    noteId: String,
    fontFamily: UiNoteFontFamily,
    fontColor: UiNoteFontColor,
    visible: Boolean,
    onFontFamilySelected: (family: UiNoteFontFamily) -> Unit,
    onFontColorSelected: (color: UiNoteFontColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<FontViewModel, FontViewModel.Factory>(
        key = FONT_CONTENT_TAG + noteId,
        creationCallback = { factory ->
            factory.create(
                initialFontColor = fontColor,
                initialFontFamily = fontFamily,
            )
        },
    )

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val density = LocalDensity.current
    val isImeVisible = WindowInsets.isImeVisible
    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - density.run { navigationBarsHeight.toDp() }

    val listState = rememberLazyGridState()

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
        }
    }

    if (isImeVisible && visible) {
        Content(
            modifier = modifier.height(contentHeight),
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onFontFamilySelected = onFontFamilySelected,
            onFontColorSelected = onFontColorSelected,
            listState = listState,
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Content(
                modifier = modifier.height(contentHeight),
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onFontFamilySelected = onFontFamilySelected,
                onFontColorSelected = onFontColorSelected,
                listState = listState,
            )
        }
    }
}

@Composable
private fun Content(
    uiState: FontPanelUiState,
    onFontFamilySelected: (family: UiNoteFontFamily) -> Unit,
    onFontColorSelected: (color: UiNoteFontColor) -> Unit,
    onEvent: (event: FontPanelEvent) -> Unit,
    listState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val spanCount = 3
    val fontSpanIndexes = remember { mutableStateMapOf<UiNoteFontFamily, Int>() }
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {}
            .drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(elevation = 2.dp)
                }
            },
        state = listState,
        columns = GridCells.Fixed(spanCount),
        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
    ) {
        item(
            span = { GridItemSpan(spanCount) }
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 20.dp,
                ),
            ) {
                items(
                    count = uiState.fontColors.count(),
                    key = { uiState.fontColors[it] },
                ) { index ->
                    ColorItem(
                        color = uiState.fontColors[index],
                        isSelected = { it == uiState.selectedFontColor },
                        onClick = { color ->
                            onFontColorSelected(color)
                            onEvent(FontPanelEvent.OnFontColorSelected(color))
                        },
                    )
                }
            }
        }
        items(
            count = uiState.fontFamilies.count(),
            key = { uiState.fontFamilies[it] },
            span = { index ->
                fontSpanIndexes[uiState.fontFamilies[index]] = index % maxLineSpan
                GridItemSpan(1)
            },
        ) { index ->
            val item = uiState.fontFamilies[index]
            FontItem(
                modifier = Modifier.padding(
                    start = if (fontSpanIndexes[item] == 0) 8.dp else 0.dp,
                    end = if (fontSpanIndexes[item] == spanCount - 1) 8.dp else 0.dp,
                ),
                family = uiState.fontFamilies[index],
                isSelected = { it == uiState.selectedFontFamily },
                onClick = { family ->
                    onFontFamilySelected(family)
                    onEvent(FontPanelEvent.OnFontFamilySelected(family))
                },
            )
        }
    }
}

@Composable
private fun ColorItem(
    color: UiNoteFontColor,
    isSelected: (color: UiNoteFontColor) -> Boolean,
    onClick: (color: UiNoteFontColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(color.value, CircleShape)
            .clickableNoRipple { onClick(color) },
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected(color)) {
            Icon(
                painter = painterResource(uiR.drawable.ic_action_done),
                contentDescription = null,
                tint = if (color == UiNoteFontColor.WHITE) {
                    Colors.Common.Gray
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

@Composable
private fun FontItem(
    family: UiNoteFontFamily,
    isSelected: (family: UiNoteFontFamily) -> Boolean,
    onClick: (family: UiNoteFontFamily) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1.8f)
            .applyIf(isSelected(family)) {
                Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(8.dp),
                )
            }
            .clickableNoRipple { onClick(family) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = family.name.lowercase(Locale.ROOT),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = family.value,
            ),
        )
    }
}

@Composable
@PreviewWithBackground
private fun PanelPreview() {
    SerenityTheme {
        FontTitleBar(
            onDoneClick = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun ContentPreview() {
    SerenityTheme {
        Content(
            uiState = FontPanelUiState(
                selectedFontColor = UiNoteFontColor.WHITE,
                selectedFontFamily = UiNoteFontFamily.QUICK_SAND,
                fontFamilies = UiNoteFontFamily.entries.toImmutableList(),
                fontColors = UiNoteFontColor.entries.toImmutableList(),
            ),
            onEvent = {},
            onFontFamilySelected = {},
            onFontColorSelected = {},
            listState = rememberLazyGridState(),
        )
    }
}
