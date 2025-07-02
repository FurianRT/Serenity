package com.furianrt.mediasorting.internal.ui.composables

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.mediasorting.internal.ui.entities.Constants
import com.furianrt.mediasorting.internal.ui.entities.MediaItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import java.time.ZonedDateTime

@Composable
internal fun ImageItem(
    item: MediaItem.Image,
    modifier: Modifier = Modifier,
    onDeleteClick: (item: MediaItem.Image) -> Unit,
    onClick: (item: MediaItem.Image) -> Unit,
) {
    val context = LocalContext.current
    val request = remember(item.id) {
        ImageRequest.Builder(context)
            .size(Constants.REQUESTED_IMAGE_SIZE)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.id)
            .data(item.uri)
            .build()
    }
    Box(modifier = modifier.aspectRatio(1f)) {
        AsyncImage(
            modifier = Modifier
                .padding(top = 4.dp, end = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick(item) }
                .fillMaxSize(),
            model = request,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentDescription = null,
        )
        DeleteChip(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { onDeleteClick(item) },
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ImageItem(
            item = MediaItem.Image(
                id = "",
                name = "",
                uri = Uri.EMPTY,
                ratio = 1f,
                addedDate = ZonedDateTime.now(),
            ),
            onDeleteClick = {},
            onClick = {},
        )
    }
}
