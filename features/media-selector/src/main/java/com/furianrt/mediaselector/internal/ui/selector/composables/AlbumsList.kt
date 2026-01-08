package com.furianrt.mediaselector.internal.ui.selector.composables

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import com.furianrt.mediaselector.internal.ui.entities.Constants
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.uikit.components.DurationBadge
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch

private val ITEM_HEIGHT = 50.dp
private val ITEM_WIDTH = 60.dp

@Composable
internal fun AlbumsList(
    albums: List<MediaAlbumItem>,
    expanded: Boolean,
    dropDownHazeState: HazeState,
    onAlbumClick: (album: MediaAlbumItem) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }
    DropdownMenu(
        modifier = modifier
            .heightIn(min = 200.dp, max = 590.dp)
            .hazeEffect(
                state = dropDownHazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 12.dp,
                )
            )
            .background(MaterialTheme.colorScheme.background),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        albums.forEachIndexed { index, album ->
            MenuItem(
                modifier = Modifier
                    .padding(
                        bottom = if (index != albums.lastIndex) 6.dp else 0.dp,
                    ),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f, fill = false),
                            text = album.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = album.mediaCount.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                        )
                    }
                },
                leadingIcon = {
                    when (album.thumbnail) {
                        is MediaAlbumItem.Thumbnail.Image -> ImageThumbnail(
                            thumbnail = album.thumbnail,
                        )

                        is MediaAlbumItem.Thumbnail.Video -> VideoThumbnail(
                            thumbnail = album.thumbnail,
                        )

                        null -> EmptyThumbnail()
                    }
                },
                animate = false,
                onClick = {
                    onAlbumClick(album)
                    onDismissRequest()
                },
            )
        }
    }
}

@Composable
private fun ImageThumbnail(
    thumbnail: MediaAlbumItem.Thumbnail.Image,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val request = remember(thumbnail.id) {
        ImageRequest.Builder(context)
            .size(Constants.REQUESTED_IMAGE_SIZE)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(thumbnail.id)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(thumbnail.id)
            .data(thumbnail.uri)
            .build()
    }
    AsyncImage(
        modifier = modifier
            .size(height = ITEM_HEIGHT, width = ITEM_WIDTH)
            .clip(RoundedCornerShape(2.dp)),
        model = request,
        contentScale = ContentScale.Crop,
        placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
        error = ColorPainter(MaterialTheme.colorScheme.tertiary),
        contentDescription = null,
    )
}

@Composable
private fun VideoThumbnail(
    thumbnail: MediaAlbumItem.Thumbnail.Video,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val request = remember(thumbnail.id) {
        ImageRequest.Builder(context)
            .size(Constants.REQUESTED_IMAGE_SIZE)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(thumbnail.id)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(thumbnail.id)
            .data(thumbnail.uri)
            .decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
            .build()
    }
    Box(
        modifier = modifier
            .size(height = ITEM_HEIGHT, width = ITEM_WIDTH)
            .clip(RoundedCornerShape(2.dp)),
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = request,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
        )
        DurationBadge(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 2.dp, bottom = 2.dp),
            duration = thumbnail.duration,
        )
    }
}

@Composable
private fun EmptyThumbnail(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(height = ITEM_HEIGHT, width = ITEM_WIDTH)
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(2.dp))
            .clip(RoundedCornerShape(2.dp)),
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        AlbumsList(
            albums = listOf(
                MediaAlbumItem(
                    id = "0",
                    name = "All Media",
                    thumbnail = MediaAlbumItem.Thumbnail.Image(
                        id = "",
                        uri = Uri.EMPTY,
                    ),
                    mediaCount = 10,
                ),
                MediaAlbumItem(
                    id = "1",
                    name = "Camera",
                    thumbnail = MediaAlbumItem.Thumbnail.Video(
                        id = "",
                        uri = Uri.EMPTY,
                        duration = 10_000,
                    ),
                    mediaCount = 10,
                ),
                MediaAlbumItem(
                    id = "2",
                    name = "Recent",
                    thumbnail = MediaAlbumItem.Thumbnail.Image(
                        id = "",
                        uri = Uri.EMPTY,
                    ),
                    mediaCount = 10,
                ),
                MediaAlbumItem(
                    id = "3",
                    name = "Screenshot",
                    thumbnail = MediaAlbumItem.Thumbnail.Image(
                        id = "",
                        uri = Uri.EMPTY,
                    ),
                    mediaCount = 10,
                ),
                MediaAlbumItem(
                    id = "4",
                    name = "Private",
                    thumbnail = MediaAlbumItem.Thumbnail.Image(
                        id = "",
                        uri = Uri.EMPTY,
                    ),
                    mediaCount = 10,
                ),
            ),
            expanded = true,
            dropDownHazeState = HazeState(),
            onDismissRequest = {},
            onAlbumClick = {},
        )
    }
}