package com.furianrt.uikit.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.toContentAlignment
import com.furianrt.uikit.entities.toContentScale
import com.furianrt.uikit.utils.brighterBy

@Composable
fun AppBackground(
    theme: UiThemeColor,
    modifier: Modifier = Modifier,
    ignoreImage: Boolean = false,
) {
    Crossfade(
        modifier = modifier,
        targetState = theme,
    ) { targetState ->
        if (!ignoreImage && targetState.image != null) {
            val context = LocalContext.current
            val request = remember(context, targetState.image.resId) {
                ImageRequest.Builder(context)
                    .data(targetState.image.resId)
                    .memoryCacheKey(targetState.image.resId.toString())
                    .build()
            }
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .background(targetState.surface),
                model = request,
                contentScale = targetState.image.scaleType.toContentScale(),
                alignment = targetState.image.scaleType.toContentAlignment(),
                contentDescription = null,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(targetState.surface, targetState.surface.brighterBy(0.1f)),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
            )
        }
    }
}

@Composable
fun AppBackgroundWithStars(
    theme: UiThemeColor,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 200000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
    )

    Box(
        modifier = modifier,
    ) {
        AppBackground(
            theme = theme,
        )
        StarsLayout(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .graphicsLayer { rotationZ = rotationAnim },
            starCount = 75,
            starRotation = { rotationAnim },
        )
        ShootingStarsLayout(
            modifier = Modifier
                .aspectRatio(1.2f)
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )
        StarsLayout(
            modifier = Modifier.fillMaxSize(),
            starCount = 75,
        )
    }
}
