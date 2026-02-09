package com.furianrt.toolspanel.internal.ui.bullet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.orFalse
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.ui.bullet.composables.BulletListItem
import com.furianrt.toolspanel.internal.ui.bullet.composables.CheckedBulletListItem
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.toolspanel.internal.ui.common.ButtonKeyboard
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.max

@Composable
internal fun BulletTitleBar(
    showKeyBoardButton: Boolean,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: BulletViewModel = hiltViewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val keyboardController = LocalSoftwareKeyboardController.current

    val onDoneClickState by rememberUpdatedState(onDoneClick)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is BulletPanelEffect.ShowKeyboard -> keyboardController?.show()
                    is BulletPanelEffect.ClosePanel -> onDoneClickState()
                }
            }
    }

    TitleContent(
        modifier = modifier,
        showKeyBoardButton = showKeyBoardButton,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun TitleContent(
    showKeyBoardButton: Boolean,
    onEvent: (event: BulletPanelEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showKeyBoardButtonState = remember { showKeyBoardButton }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        if (showKeyBoardButtonState) {
            ButtonKeyboard(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterStart),
                onClick = { onEvent(BulletPanelEvent.OnKeyboardClick) },
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 40.dp),
            text = stringResource(R.string.bullet_panel_title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
        )
        ButtonClose(
            modifier = Modifier
                .padding(end = 4.dp)
                .align(Alignment.CenterEnd),
            onClick = { onEvent(BulletPanelEvent.OnCloseClick) },
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.BottomCenter),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BulletContent(
    visible: Boolean,
    titleState: NoteTitleState?,
    modifier: Modifier = Modifier,
) {
    val viewModel: BulletViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val density = LocalDensity.current

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val isImeVisible = WindowInsets.isImeVisible
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - density.run { navigationBarsHeight.toDp() }

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
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
                titleState = titleState,
                uiState = uiState,
            )
        }
    }
}

@Composable
private fun Content(
    uiState: BulletPanelUiState,
    titleState: NoteTitleState?,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState()

    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val spanCount = 3
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
    ) {
        itemsIndexed(items = uiState.items) { _, item ->
            val hasBulletLIst = remember(titleState?.annotatedString, titleState?.selection) {
                titleState?.hasBulletList(bulletList = item).orFalse()
            }
            if (item is NoteTitleState.BulletListType.Checked) {
                CheckedBulletListItem(
                    uncheckedBullet = item.bullet,
                    checkedBullet = item.doneBullet,
                    isSelected = hasBulletLIst,
                    onClick = {
                        if (hasBulletLIst) {
                            titleState?.removeBulletList(item)
                        } else {
                            titleState?.addBulletList(item)
                        }
                    },
                )
            } else {
                BulletListItem(
                    bullet = item.bullet,
                    isSelected = hasBulletLIst,
                    onClick = {
                        if (hasBulletLIst) {
                            titleState?.removeBulletList(item)
                        } else {
                            titleState?.addBulletList(item)
                        }
                    },
                )
            }
        }
    }
}

@Composable
@PreviewWithBackground
private fun PanelPreview() {
    SerenityTheme {
        TitleContent(
            showKeyBoardButton = true,
            onEvent = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun ContentPreview() {
    SerenityTheme {
        Content(
            uiState = BulletPanelUiState(
                items = listOf(
                    NoteTitleState.BulletListType.Checked(),
                    NoteTitleState.BulletListType.Dots,
                    NoteTitleState.BulletListType.Done,
                    NoteTitleState.BulletListType.Moon,
                    NoteTitleState.BulletListType.Sun,
                    NoteTitleState.BulletListType.Candle,
                    NoteTitleState.BulletListType.Knife,
                    NoteTitleState.BulletListType.Scroll,
                ),
            ),
            titleState = NoteTitleState(
                fontFamily = UiNoteFontFamily.NotoSans,
            ),
        )
    }
}
