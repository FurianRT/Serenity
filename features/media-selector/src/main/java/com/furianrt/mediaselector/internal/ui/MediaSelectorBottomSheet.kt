package com.furianrt.mediaselector.internal.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.furianrt.core.buildImmutableList
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.internal.ui.MediaSelectorEvent.OnPartialAccessMessageClick
import com.furianrt.mediaselector.internal.ui.MediaSelectorUiState.*
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.storage.api.repositories.DeviceMediaRepository
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest

private const val PARTIAL_ACCESS_ITEM_KEY = "partial_access"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MediaSelectorBottomSheetInternal(
    navHostController: NavHostController,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MediaSelectorViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = DeviceMediaRepository.getMediaPermissionList(),
        onPermissionsResult = {
            viewModel.onEvent(MediaSelectorEvent.OnMediaPermissionsSelected)
        },
    )

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is MediaSelectorEffect.RequestMediaPermission -> {
                        storagePermissionsState.launchMultiplePermissionRequest()
                    }
                }
            }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = modifier
            .padding(top = ToolbarConstants.toolbarHeight)
            .fillMaxSize(),
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets.statusBars },
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = null,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f)),
        ) {
            DragHandle()
            when (uiState.screenState) {
                is ScreenState.Loading -> LoadingContent(uiState.showPartialAccessMessage)
                is ScreenState.Empty -> EmptyContent(uiState.showPartialAccessMessage)
                is ScreenState.Success -> SuccessContent(
                    screenState = uiState.screenState,
                    showPartialAccessMessage = uiState.showPartialAccessMessage,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    screenState: ScreenState.Success,
    showPartialAccessMessage: Boolean,
    onEvent: (event: MediaSelectorEvent) -> Unit,
) {
    val listSpanCount = 3
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        state = screenState.listState,
        columns = GridCells.Fixed(listSpanCount),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
    ) {
        if (showPartialAccessMessage) {
            item(
                key = PARTIAL_ACCESS_ITEM_KEY,
                span = { GridItemSpan(listSpanCount) },
                content = { PermissionsMessage(onClick = { onEvent(OnPartialAccessMessageClick) }) }
            )
        }

        items(
            count = screenState.items.count(),
            key = { screenState.items[it].id },
            contentType = { screenState.items[it].javaClass.name },
        ) { index ->
            when (val item = screenState.items[index]) {
                is MediaItem.Image -> ImageItem(
                    modifier = Modifier.clip(
                        RoundedCornerShape(
                            topStart = if (index == 0) 8.dp else 0.dp,
                            topEnd = if (index == listSpanCount - 1) 8.dp else 0.dp,
                        )
                    ),
                    item = item,
                )

                is MediaItem.Video -> VideoItem(
                    modifier = Modifier.clip(
                        RoundedCornerShape(
                            topStart = if (index == 0) 8.dp else 0.dp,
                            topEnd = if (index == listSpanCount - 1) 8.dp else 0.dp,
                        )
                    ),
                    item = item,
                )
            }
        }
    }
}

@Composable
private fun ImageItem(
    item: MediaItem.Image,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.aspectRatio(1f)) {
        AsyncImage(
            modifier = modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .size(width = 300, height = 300)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .memoryCacheKey(item.id.toString())
                .data(item.uri)
                .build(),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
        )
        CheckBox(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
            isChecked = item.isSelected,
            onToggle = {},
        )
    }
}

@Composable
private fun VideoItem(
    item: MediaItem.Video,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.aspectRatio(1f)) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .size(width = 300, height = 300)
                .data(item.uri)
                .decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
                .build(),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
        )
        DurationBadge(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 4.dp, bottom = 4.dp),
            duration = item.duration,
        )
        CheckBox(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
            isChecked = item.isSelected,
            onToggle = {},
        )
    }
}

@Composable
private fun PermissionsMessage(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_warning),
            tint = Color.Unspecified,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.partial_access_message),
            style = MaterialTheme.typography.labelMedium,
        )
        Icon(
            painter = painterResource(R.drawable.ic_forward),
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@Composable
private fun DurationBadge(
    duration: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = duration,
            fontSize = 12.sp,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun CheckBox(
    isChecked: Boolean,
    onToggle: (isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .size(24.dp)
            .background(color = Color.Black.copy(alpha = 0.1f), shape = CircleShape)
            .border(width = 1.5.dp, color = Color.White, shape = CircleShape),
    )
}

@Composable
private fun LoadingContent(
    showPartialAccessMessage: Boolean,
) {
    Spacer(
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun EmptyContent(
    showPartialAccessMessage: Boolean,
) {
    Spacer(
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(
            modifier = Modifier
                .size(40.dp, 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun SuccessContentPreview() {
    SerenityTheme {
        SuccessContent(
            showPartialAccessMessage = true,
            onEvent = {},
            screenState = ScreenState.Success(
                items = buildImmutableList {
                    repeat(19) { index ->
                        val item = if (index % 2 == 0) {
                            MediaItem.Image(
                                id = index.toLong(),
                                uri = Uri.EMPTY,
                                title = "Test title $index",
                                isSelected = index == 4,
                            )
                        } else {
                            MediaItem.Video(
                                id = index + 10L,
                                uri = Uri.EMPTY,
                                title = "Test title $index",
                                isSelected = index == 7,
                                duration = "0:10",
                            )
                        }
                        add(item)
                    }
                },
                listState = LazyGridState(),
            ),
        )
    }
}
