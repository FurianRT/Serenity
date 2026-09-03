package com.furianrt.gallery.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.gallery.internal.ui.entities.ListItem

@Composable
internal fun PhotoItem(
    item: ListItem.Photo,
    background: Color,
    onClick: (item: ListItem.Photo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .rotate(item.rotation)
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 10.dp,
                    color = Color.Black.copy(alpha = 0.15f),
                )
            )
            .clip(RoundedCornerShape(2.dp))
            .background(background)
            .clickable { onClick(item) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .padding(top = 6.dp, start = 6.dp, end = 6.dp)
                .fillMaxSize(),
            item = item,
        )
        Label(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 8.dp),
            text = item.label,
        )
    }
}

@Composable
private fun Image(
    item: ListItem.Photo,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val request = remember(context, item.id, item.uri) {
        ImageRequest.Builder(context)
            .size(200)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(item.id)
            .data(item.uri)
            .build()
    }
    AsyncImage(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .aspectRatio(1f),
        model = request,
        contentScale = ContentScale.Crop,
        placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
        error = ColorPainter(MaterialTheme.colorScheme.tertiary),
        contentDescription = null,
    )
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontSize = 10.sp,
            letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 0.7f,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
