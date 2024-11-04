package com.furianrt.mediaselector.internal.ui.selector

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnPartialAccessMessageClick
import com.furianrt.mediaselector.internal.ui.selector.composables.ImageItem
import com.furianrt.mediaselector.internal.ui.selector.composables.PermissionsMessage
import com.furianrt.mediaselector.internal.ui.selector.composables.SelectedCountHint
import com.furianrt.mediaselector.internal.ui.selector.composables.VideoItem
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.uikit.components.ActionButton
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

private const val ACTION_PANEL_ANIM_DURATION = 250

@Composable
internal fun SuccessContent(
    uiState: MediaSelectorUiState.Success,
    listState: LazyGridState,
    onEvent: (event: MediaSelectorEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = remember { HazeState() }
    val listSpanCount = 3
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState),
            state = listState,
            columns = GridCells.Fixed(listSpanCount),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(
                start = 4.dp,
                end = 4.dp,
                bottom = 56.dp + bottomInsetPadding
            ),
        ) {
            if (uiState.showPartialAccessMessage) {
                item(span = { GridItemSpan(listSpanCount) }) {
                    PermissionsMessage(onClick = { onEvent(OnPartialAccessMessageClick) })
                }
            }

            items(
                count = uiState.items.count(),
                key = { uiState.items[it].id },
                contentType = { uiState.items[it].javaClass.name },
            ) { index ->
                when (val item = uiState.items[index]) {
                    is MediaItem.Image -> ImageItem(
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = if (index == 0) 8.dp else 0.dp,
                                topEnd = if (index == listSpanCount - 1) 8.dp else 0.dp,
                            )
                        ),
                        item = item,
                        onSelectClick = { onEvent(MediaSelectorEvent.OnSelectItemClick(it)) },
                        onClick = { onEvent(MediaSelectorEvent.OnMediaClick(it.id)) },
                    )

                    is MediaItem.Video -> VideoItem(
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = if (index == 0) 8.dp else 0.dp,
                                topEnd = if (index == listSpanCount - 1) 8.dp else 0.dp,
                            )
                        ),
                        item = item,
                        onSelectClick = { onEvent(MediaSelectorEvent.OnSelectItemClick(it)) },
                        onClick = { onEvent(MediaSelectorEvent.OnMediaClick(it.id)) },
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = uiState.selectedCount > 0,
            enter = fadeIn(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + slideIn(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                initialOffset = { IntOffset(0, it.height) },
            ),
            exit = fadeOut(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + slideOut(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                targetOffset = { IntOffset(0, it.height) },
            )
        ) {
            SelectedCountHint(
                modifier = Modifier
                    .fillMaxWidth()
                    .hazeChild(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            blurRadius = 12.dp,
                        ),
                    )
                    .padding(16.dp)
                    .navigationBarsPadding(),
                count = uiState.selectedCount,
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .wrapContentSize()
                .padding(end = 16.dp, bottom = 24.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomEnd),
            visible = uiState.selectedCount > 0,
            enter = fadeIn(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
            ),
            exit = fadeOut(animationSpec = tween(ACTION_PANEL_ANIM_DURATION)) + scaleOut(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                targetScale = 0.5f,
            )
        ) {
            ActionButton(
                icon = painterResource(R.drawable.ic_send),
                onClick = { onEvent(MediaSelectorEvent.OnSendClick) },
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            onEvent = {},
            listState = rememberLazyGridState(),
            uiState = MediaSelectorUiState.Success(
                items = buildImmutableList {
                    repeat(18) { index ->
                        val item = if (index % 2 == 0) {
                            MediaItem.Image(
                                id = index.toLong(),
                                name = index.toString(),
                                uri = Uri.EMPTY,
                                ratio = 1f,
                                state = if (index == 4) {
                                    SelectionState.Selected(1)
                                } else {
                                    SelectionState.Default
                                },
                            )
                        } else {
                            MediaItem.Video(
                                id = index + 10L,
                                name = index.toString(),
                                uri = Uri.EMPTY,
                                ratio = 1f,
                                state = if (index == 9) {
                                    SelectionState.Selected(2)
                                } else {
                                    SelectionState.Default
                                },
                                duration = 10 * 60 * 1000,
                            )
                        }
                        add(item)
                    }
                },
                selectedCount = 2,
                showPartialAccessMessage = true,
            ),
        )
    }
}
