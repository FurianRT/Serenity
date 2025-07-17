package com.furianrt.toolspanel.internal.ui.bullet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.ui.bullet.composables.DotsItem
import com.furianrt.toolspanel.internal.ui.bullet.entities.BulletListItem
import com.furianrt.toolspanel.internal.ui.bullet.extensions.toBulletListType
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.max
import com.furianrt.uikit.R as uiR

@Composable
internal fun BulletTitleBar(
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: BulletViewModel = hiltViewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onDoneClickState by rememberUpdatedState(onDoneClick)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                if (effect is BulletPanelEffect.ClosePanel) {
                    onDoneClickState()
                }
            }
    }

    TitleContent(
        modifier = modifier,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun TitleContent(
    onEvent: (event: BulletPanelEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.bullet_panel_title),
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(
            modifier = Modifier
                .padding(end = 4.dp)
                .align(Alignment.CenterEnd),
            onClick = { onEvent(BulletPanelEvent.OnCloseClick) },
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_exit),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun BulletContent(
    visible: Boolean,
    titleState: NoteTitleState,
    modifier: Modifier = Modifier,
) {
    val viewModel: BulletViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val density = LocalDensity.current

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val isImeVisible = WindowInsets.isImeVisible
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - density.run { navigationBarsHeight.toDp() }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is BulletPanelEffect.ClosePanel -> Unit
                }
            }
    }

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
                onEvent = viewModel::onEvent,
            )
        }
    }
}

@Composable
private fun Content(
    uiState: BulletPanelUiState,
    titleState: NoteTitleState,
    onEvent: (event: BulletPanelEvent) -> Unit,
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
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
    ) {
        itemsIndexed(items = uiState.items) { _, item ->
            val bulletListType = item.toBulletListType()
            val hasBulletLIst = remember(titleState.annotatedString, titleState.selection) {
                titleState.hasBulletList(
                    position = titleState.selection.min,
                    bulletList = bulletListType,
                )
            }
            when (item) {
                is BulletListItem.Dots -> DotsItem(
                    isSelected = hasBulletLIst,
                    onClick = {
                        if (hasBulletLIst) {
                            titleState.removeBulletList(titleState.selection.min, bulletListType)
                        } else {
                            titleState.addBulletList(titleState.selection.min, bulletListType)
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
                items = listOf(BulletListItem.Dots(isPremium = true)),
            ),
            titleState = NoteTitleState(
                fontFamily = UiNoteFontFamily.QuickSand,
            ),
            onEvent = {},
        )
    }
}
