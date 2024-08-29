package com.furianrt.mediaselector.internal.ui.composables

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.furianrt.mediaselector.internal.ui.entities.Constants
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun ImageItem(
    item: MediaItem.Image,
    onSelectClick: (item: MediaItem.Image) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageScaleValue by animateFloatAsState(
        targetValue = if (item.isSelected) Constants.SELECTED_ITEM_SCALE else 1f,
        animationSpec = tween(durationMillis = Constants.IMAGE_SCALE_ANIM_DURATION),
        label = "ItemScale"
    )
    Box(modifier = modifier.aspectRatio(1f)) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = imageScaleValue
                    scaleY = imageScaleValue
                },
            model = ImageRequest.Builder(LocalContext.current)
                .size(Constants.REQUESTED_IMAGE_SIZE)
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
            state = item.state,
            onClick = { onSelectClick(item) },
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ImageItem(
            item = MediaItem.Image(
                id = 0L,
                uri = Uri.EMPTY,
                title = "Test",
                state = SelectionState.Selected(1),
            ),
            onSelectClick = {},
        )
    }
}
