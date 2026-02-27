package com.furianrt.apptheme.internal.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.uikit.R as uiR
import com.furianrt.apptheme.internal.ui.composables.ThemeItem
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun AppThemeScreen(
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: AppThemeViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is AppThemeEffect.CloseScreen -> onCloseRequestState()
                }
            }
    }

    Content(
        modifier = modifier,
        state = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    state: AppThemeState,
    onEvent: (event: AppThemeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val toolbarState = remember { MovableToolbarState() }
    val listState = rememberLazyGridState()

    MovableToolbarScaffold(
        modifier = modifier,
        state = toolbarState,
        listState = listState,
        blurRadius = 12.dp,
        blurAlpha = 0.5f,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(uiR.string.title_theme),
                onBackClick = { onEvent(AppThemeEvent.OnBackClick) },
            )
        }
    ) { topPadding ->
        Crossfade(
            targetState = state.theme,
        ) { targetState ->
            AppBackground(
                theme = targetState,
            )
        }
        when (state.content) {
            is AppThemeState.Content.Loading -> LoadingContent()
            is AppThemeState.Content.Success -> SuccessContent(
                state = state.content,
                onEvent = onEvent,
                topPadding = topPadding,
                listState = listState,
            )
        }
    }
}

@Composable
private fun SuccessContent(
    state: AppThemeState.Content.Success,
    onEvent: (event: AppThemeEvent) -> Unit,
    listState: LazyGridState,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        state = listState,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = topPadding + 16.dp,
            bottom = bottomInsetPadding + 16.dp,
        ),
    ) {
        itemsIndexed(
            items = state.themes,
            key = { _, item -> item.id },
        ) { _, item ->
            ThemeItem(
                theme = item,
                isSelected = item.id == state.selectedId,
                onClick = { onEvent(AppThemeEvent.OnThemeClick(it)) },
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}
