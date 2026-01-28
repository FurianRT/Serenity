package com.furianrt.toolspanel.internal.ui.background.solid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.domain.NoteThemesHolder
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundSelectedThemeProvider
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

private const val NOTE_BACKGROUND_TAG = "note_panel_solid_background"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SolidBackgroundContent(
    noteId: String,
    selectedThemeProvider: BackgroundSelectedThemeProvider,
    onThemeSelected: (theme: UiNoteTheme.Solid?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<SolidBackgroundViewModel, SolidBackgroundViewModel.Factory>(
        key = NOTE_BACKGROUND_TAG + noteId,
        creationCallback = { it.create(selectedThemeProvider = selectedThemeProvider) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is SolidBackgroundEffect.OnThemeSelected -> onThemeSelected(effect.theme)
                }
            }
    }

    Content(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    uiState: SolidBackgroundUiState,
    onEvent: (event: SolidBackgroundEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState(
        initialFirstVisibleItemIndex = uiState.selectedThemeIndex ?: 0,
    )
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {}
            .drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(color = shadowColor)
                }
            },
        state = listState,
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
    ) {
        item {
            ClearItem(
                onClick = { onEvent(SolidBackgroundEvent.OnClearBackgroundClick) },
            )
        }
        itemsIndexed(items = uiState.themes) { index, theme ->
            ThemeItem(
                theme = theme,
                isSelected = index == uiState.selectedThemeIndex,
                onClick = { onEvent(SolidBackgroundEvent.OnThemeSelected(it)) },
            )
        }
    }
}

@Composable
private fun ThemeItem(
    theme: UiNoteTheme.Solid,
    isSelected: Boolean,
    onClick: (theme: UiNoteTheme.Solid) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .applyIf(isSelected) {
                Modifier
                    .border(
                        width = 1.dp,
                        color = theme.color.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(3.dp)
            }
            .clickableNoRipple { onClick(theme) },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = theme.color.colorScheme.surface,
                    shape = RoundedCornerShape(if (isSelected) 14.dp else 16.dp),
                )
        )
    }
}

@Composable
private fun ClearItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .alpha(0.3f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .clickableWithScaleAnim(onClick = onClick, maxScale = 1.2f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_background_clear),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    val holder = NoteThemesHolder()
    SerenityTheme {
        Content(
            uiState = SolidBackgroundUiState(
                themes = holder.getSolidThemes(),
                selectedThemeIndex = 0,
            ),
            onEvent = {},
        )
    }
}
