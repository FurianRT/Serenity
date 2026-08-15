package com.furianrt.mediaselector.internal.ui.selector

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.mediaselector.internal.ui.selector.composables.BottomPanel
import com.furianrt.mediaselector.internal.ui.selector.composables.CameraItem
import com.furianrt.mediaselector.internal.ui.selector.composables.ImageItem
import com.furianrt.mediaselector.internal.ui.selector.composables.VerticalScrollBar
import com.furianrt.mediaselector.internal.ui.selector.composables.VideoItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.UserScrollState
import com.furianrt.uikit.utils.rememberUserInputScrollConnection
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

private const val BOTTOM_PANEL_SHOW_DELAY = 500L

private const val CAMERA_ITEM_KEY = "camera_item"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SuccessContent(
    uiState: MediaSelectorUiState.Success,
    listState: LazyGridState,
    albumsDialogState: List<MediaAlbumItem>?,
    sheetState: SheetState,
    onEvent: (event: MediaSelectorEvent) -> Unit,
    onBarDragStart: () -> Unit,
    onBarDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = rememberHazeState()
    val listSpanCount = 3
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val userScrollConnection = rememberUserInputScrollConnection()

    var showBottomPanel by remember { mutableStateOf(true) }

    val showScrollBar by remember {
        derivedStateOf {
            sheetState.targetValue == SheetValue.Expanded &&
                    (listState.canScrollForward ||
                            listState.canScrollBackward)
        }
    }

    LaunchedEffect(userScrollConnection.scrollState) {
        when (userScrollConnection.scrollState) {
            UserScrollState.SCROLLING_DOWN -> showBottomPanel = false
            UserScrollState.SCROLLING_UP -> showBottomPanel = true
            UserScrollState.IDLE -> {
                delay(BOTTOM_PANEL_SHOW_DELAY.milliseconds)
                showBottomPanel = true
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(userScrollConnection)
                .hazeSource(state = hazeState),
            state = listState,
            columns = GridCells.Fixed(listSpanCount),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            overscrollEffect = null,
            contentPadding = PaddingValues(
                start = 4.dp,
                end = 4.dp,
                bottom = 56.dp + bottomInsetPadding
            ),
        ) {
            item(key = CAMERA_ITEM_KEY) {
                CameraItem(
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
                    allowVideo = uiState.allowVideo,
                    onPhotoClick = { onEvent(MediaSelectorEvent.OnCameraPhotoItemClick) },
                    onVideoClick = { onEvent(MediaSelectorEvent.OnCameraVideoItemClick) },
                )
            }

            items(
                count = uiState.items.size,
                key = { uiState.items[it].id },
                contentType = { uiState.items[it].javaClass.name },
            ) { index ->
                when (val item = uiState.items[index]) {
                    is MediaItem.Image -> ImageItem(
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topEnd = if (index == listSpanCount - 1) 8.dp else 2.dp,
                                topStart = 2.dp,
                                bottomStart = 2.dp,
                                bottomEnd = 2.dp,
                            )
                        ),
                        item = item,
                        onSelectClick = { onEvent(MediaSelectorEvent.OnSelectItemClick(it)) },
                        onClick = { onEvent(MediaSelectorEvent.OnMediaClick(it.id)) },
                    )

                    is MediaItem.Video -> VideoItem(
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topEnd = if (index == listSpanCount - 1) 8.dp else 2.dp,
                                topStart = 2.dp,
                                bottomStart = 2.dp,
                                bottomEnd = 2.dp,
                            )
                        ),
                        item = item,
                        onSelectClick = { onEvent(MediaSelectorEvent.OnSelectItemClick(it)) },
                        onClick = { onEvent(MediaSelectorEvent.OnMediaClick(it.id)) },
                    )
                }
            }
        }

        BottomPanel(
            modifier = Modifier.offset {
                IntOffset(x = 0, y = -sheetState.requireOffset().toInt())
            },
            selectedAlbum = uiState.selectedAlbum,
            selectedCount = uiState.selectedCount,
            visible = showBottomPanel,
            albumsDialogState = albumsDialogState,
            hazeState = hazeState,
            onSendClick = { onEvent(MediaSelectorEvent.OnSendClick) },
            onAlbumsClick = { onEvent(MediaSelectorEvent.OnAlbumsClick) },
            onAlbumSelected = { onEvent(MediaSelectorEvent.OnAlbumSelected(it)) },
            onAlbumsDismissed = { onEvent(MediaSelectorEvent.OnAlbumsDismissed) },
        )
        if (showScrollBar) {
            VerticalScrollBar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(top = 4.dp, bottom = 100.dp),
                columns = listSpanCount,
                verticalSpacing = 4.dp,
                listState = listState,
                itemCount = uiState.items.size + 1,
                hazeState = hazeState,
                dateProvider = { uiState.items.getOrNull(it)?.date ?: LocalDate.now() },
                onDragStart = onBarDragStart,
                onDragEnd = onBarDragEnd,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        SuccessContent(
            onEvent = {},
            onBarDragStart = {},
            onBarDragEnd = {},
            listState = rememberLazyGridState(),
            albumsDialogState = null,
            sheetState = rememberModalBottomSheetState(),
            uiState = MediaSelectorUiState.Success(
                items = buildList {
                    repeat(18) { index ->
                        val item = if (index % 2 == 0) {
                            MediaItem.Image(
                                id = index.toLong(),
                                name = index.toString(),
                                uri = Uri.EMPTY,
                                ratio = 1f,
                                state = if (index == 4) {
                                    SelectionState.Counter(1)
                                } else {
                                    SelectionState.Default
                                },
                                date = LocalDate.now(),
                                album = MediaItem.Album(
                                    id = "1",
                                    name = "Camera",
                                ),
                            )
                        } else {
                            MediaItem.Video(
                                id = index + 10L,
                                name = index.toString(),
                                uri = Uri.EMPTY,
                                ratio = 1f,
                                state = if (index == 9) {
                                    SelectionState.Counter(2)
                                } else {
                                    SelectionState.Default
                                },
                                duration = 10 * 60 * 1000,
                                date = LocalDate.now(),
                                album = MediaItem.Album(
                                    id = "2",
                                    name = "Recent",
                                ),
                            )
                        }
                        add(item)
                    }
                },
                selectedCount = 2,
                selectedAlbum = MediaAlbumItem(
                    id = "",
                    name = "Albums",
                    thumbnail = null,
                    mediaCount = 10,
                ),
                allowVideo = true,
                showPartialAccessMessage = true,
            ),
        )
    }
}
