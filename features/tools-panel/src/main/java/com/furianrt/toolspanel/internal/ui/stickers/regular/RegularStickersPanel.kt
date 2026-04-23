package com.furianrt.toolspanel.internal.ui.stickers.regular

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.domain.StickersHolder
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

private const val NOTE_REGULAR_STICKERS_TAG = "note_panel_regular_stickers"

@Composable
internal fun RegularStickersPanel(
    noteId: String,
    packId: String,
    onStickerSelected: (sticker: Sticker) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<RegularStickersViewModel, RegularStickersViewModel.Factory>(
        key = NOTE_REGULAR_STICKERS_TAG + noteId + packId,
        creationCallback = { it.create(packId = packId) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onStickerSelectedState by rememberUpdatedState(onStickerSelected)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is RegularStickersEffect.SelectSticker -> onStickerSelectedState(effect.sticker)
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
    uiState: RegularStickersUiState,
    onEvent: (event: RegularStickersEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState()
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
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
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
    ) {
        items(
            count = uiState.pack.stickers.size,
            key = { uiState.pack.stickers[it].id },
        ) { index ->
            ContentItem(
                sticker = uiState.pack.stickers[index],
                onClick = { onEvent(RegularStickersEvent.OnStickerSelected(it)) },
            )
        }
    }
}

@Composable
private fun ContentItem(
    sticker: Sticker,
    onClick: (sticker: Sticker) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(sticker) }
            .padding(horizontal = 2.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = sticker.iconData,
            contentDescription = null,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        Content(
            uiState = RegularStickersUiState(
                pack = StickersHolder(StickersRepository.mock()).getStickersPacks().first(),
            ),
            onEvent = {},
        )
    }
}
