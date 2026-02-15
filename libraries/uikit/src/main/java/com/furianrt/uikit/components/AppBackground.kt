package com.furianrt.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.toContentAlignment
import com.furianrt.uikit.entities.toContentScale

@Composable
fun AppBackground(
    theme: UiThemeColor,
    modifier: Modifier = Modifier,
) {
    if (theme.image != null) {
        AsyncImage(
            modifier = modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(theme.image.resId)
                .memoryCacheKey(theme.image.resId.toString())
                .build(),
            contentScale = theme.image.scaleType.toContentScale(),
            alignment = theme.image.scaleType.toContentAlignment(),
            contentDescription = null,
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(theme.surface)
        )
    }
}