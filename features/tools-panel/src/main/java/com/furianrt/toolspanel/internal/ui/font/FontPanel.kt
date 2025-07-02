package com.furianrt.toolspanel.internal.ui.font

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.ui.common.ColorItem
import com.furianrt.toolspanel.internal.ui.common.ColorResetItem
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.max
import com.furianrt.uikit.R as uiR

internal var cachedImeHeight = 320.dp
private const val FONT_CONTENT_TAG = "font_content"
private const val MIN_FONT_SIZE = 8f
private const val MAX_FONT_SIZE = 32f

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
    fontFamily: UiNoteFontFamily?,
    fontColor: UiNoteFontColor?,
    fontSize: Int,
    visible: Boolean,
    onFontFamilySelected: (family: UiNoteFontFamily?) -> Unit,
    onFontColorSelected: (color: UiNoteFontColor?) -> Unit,
    onFontSizeSelected: (size: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<FontViewModel, FontViewModel.Factory>(
        key = FONT_CONTENT_TAG + noteId,
        creationCallback = { factory ->
            factory.create(
                initialFontColor = fontColor,
                initialFontFamily = fontFamily,
                initialFontSize = fontSize,
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
    val colorsListState = rememberLazyListState()

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            val itemHalfSize = colorsListState.layoutInfo.visibleItemsInfo
                .firstOrNull()?.size?.div(-2) ?: 0
            colorsListState.requestScrollToItem(
                index = uiState.fontColors.indexOf(uiState.selectedFontColor).coerceAtLeast(0),
                scrollOffset = itemHalfSize,
            )
            listState.requestScrollToItem(0)
        }
    }

    if (visible || !isImeVisible) {
        AnimatedVisibility(
            modifier = modifier,
            visible = visible,
            enter = if (isImeVisible) {
                EnterTransition.None
            } else {
                expandVertically(expandFrom = Alignment.Top)
            },
            exit = if (isImeVisible) {
                ExitTransition.None
            } else {
                shrinkVertically(shrinkTowards = Alignment.Top)
            },
        ) {
            Content(
                modifier = Modifier.height(contentHeight),
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onFontFamilySelected = onFontFamilySelected,
                onFontColorSelected = onFontColorSelected,
                onFontSizeSelected = onFontSizeSelected,
                listState = listState,
                colorsListState = colorsListState,
            )
        }
    }
}

@Composable
private fun Content(
    uiState: FontPanelUiState,
    onFontFamilySelected: (family: UiNoteFontFamily?) -> Unit,
    onFontColorSelected: (color: UiNoteFontColor?) -> Unit,
    onFontSizeSelected: (size: Int) -> Unit,
    onEvent: (event: FontPanelEvent) -> Unit,
    listState: LazyGridState,
    colorsListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val spanCount = 3
    val fontSpanIndexes = remember { mutableStateMapOf<UiNoteFontFamily, Int>() }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {}
            .drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(color = shadowColor)
                }
            },
        state = listState,
        columns = GridCells.Fixed(spanCount),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item(
            span = { GridItemSpan(spanCount) }
        ) {
            SizeSelector(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                size = uiState.selectedFontSize,
                onSizeSelected = { size ->
                    onFontSizeSelected(size)
                    onEvent(FontPanelEvent.OnFontSizeSelected(size))
                },
            )
        }

        item(
            span = { GridItemSpan(spanCount) }
        ) {
            LazyRow(
                state = colorsListState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 24.dp,
                ),
            ) {
                item(key = "default") {
                    ColorResetItem(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            onFontColorSelected(null)
                            onEvent(FontPanelEvent.OnFontColorSelected(null))
                        },
                    )
                }
                items(
                    count = uiState.fontColors.count(),
                    key = { uiState.fontColors[it] },
                ) { index ->
                    val item = uiState.fontColors[index]
                    ColorItem(
                        modifier = Modifier.size(40.dp),
                        color = item.value,
                        isSelected = item == uiState.selectedFontColor,
                        onClick = { color ->
                            val uiColor = color?.let(UiNoteFontColor::fromColor)
                            onFontColorSelected(uiColor)
                            onEvent(FontPanelEvent.OnFontColorSelected(uiColor))
                        },
                    )
                }
            }
        }
        if (uiState.defaultFontFamily != null) {
            item(key = "default") {
                FontItem(
                    modifier = Modifier.padding(start = 8.dp),
                    name = "Default",
                    family = uiState.defaultFontFamily,
                    isSelected = { uiState.selectedFontFamily == null },
                    onClick = {
                        onFontFamilySelected(null)
                        onEvent(FontPanelEvent.OnFontFamilySelected(family = null))
                    },
                )
            }
        }
        items(
            count = uiState.fontFamilies.count(),
            key = { uiState.fontFamilies[it].name },
            span = { index ->
                fontSpanIndexes[uiState.fontFamilies[index]] = (index + 1) % maxLineSpan
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SizeSelector(
    size: Int,
    onSizeSelected: (size: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.font_panel_size_selector_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        Slider(
            modifier = Modifier
                .padding(top = 4.dp)
                .weight(1f),
            value = size.toFloat(),
            onValueChange = { value ->
                val newValue = value.toInt()
                if (size != newValue) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    onSizeSelected(newValue)
                }
            },
            valueRange = MIN_FONT_SIZE..MAX_FONT_SIZE,
            steps = MAX_FONT_SIZE.toInt() - MIN_FONT_SIZE.toInt() + 1,
            track = { state ->
                SliderTrack(
                    progress = (state.value - state.valueRange.start) /
                            (state.valueRange.endInclusive - state.valueRange.start),
                )
            },
            thumb = { SliderThumb() },
        )
        Text(
            text = size.toString(),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SliderTrack(
    progress: Float,
) {
    val trackColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .padding(bottom = 2.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .height(4.dp)
            .background(MaterialTheme.colorScheme.primary.copy(0.5f))
            .drawWithCache {
                onDrawBehind {
                    drawRect(color = trackColor, size = size.copy(width = size.width * progress))
                }
            },
    )
}

@Composable
private fun SliderThumb() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(14.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun FontItem(
    family: UiNoteFontFamily,
    isSelected: (family: UiNoteFontFamily) -> Boolean,
    onClick: (family: UiNoteFontFamily) -> Unit,
    modifier: Modifier = Modifier,
    name: String? = null,
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
            .clickableNoRipple { onClick(family) }
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name ?: family.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = family.regular,
                lineHeight = 20.sp,
            ),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
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
                selectedFontFamily = UiNoteFontFamily.QuickSand,
                selectedFontSize = 15,
                fontFamilies = persistentListOf(UiNoteFontFamily.QuickSand),
                fontColors = UiNoteFontColor.entries.toImmutableList(),
                defaultFontFamily = UiNoteFontFamily.QuickSand,
            ),
            onEvent = {},
            onFontFamilySelected = {},
            onFontColorSelected = {},
            onFontSizeSelected = {},
            listState = rememberLazyGridState(),
            colorsListState = rememberLazyListState(),
        )
    }
}
