package com.furianrt.uikit.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.furianrt.uikit.entities.UiAppTheme
import com.furianrt.uikit.entities.UiThemeImage
import com.furianrt.uikit.entities.toContentAlignment
import com.furianrt.uikit.entities.toContentScale
import com.furianrt.uikit.utils.brighterBy

@Composable
fun AppBackground(
    theme: UiAppTheme,
    modifier: Modifier = Modifier,
    ignoreImage: Boolean = false,
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = theme,
            contentKey = { it.id },
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        ) { targetState ->
            val image = targetState.image
            if (!ignoreImage && image != null) {
                val context = LocalContext.current
                val request = remember(context, image.source) {
                    ImageRequest.Builder(context)
                        .data(
                            when (image.source) {
                                is UiThemeImage.Source.Link -> image.source.uri
                                is UiThemeImage.Source.ResId -> image.source.resId
                            },
                        )
                        .memoryCacheKey(theme.id)
                        .build()
                }
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(targetState.colorScheme.surface),
                    model = request,
                    contentScale = image.scaleType.toContentScale(),
                    alignment = image.scaleType.toContentAlignment(),
                    contentDescription = null,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    targetState.colorScheme.surface,
                                    targetState.colorScheme.surface.brighterBy(0.12f)
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun AppBackgroundWithStars(
    theme: UiAppTheme,
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
            starCount = 70,
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
            starCount = 70,
        )
    }
}
