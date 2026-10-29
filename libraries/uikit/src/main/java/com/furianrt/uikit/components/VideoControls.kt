package com.furianrt.uikit.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.toTimeString
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.ChromaticAberrationMode
import dev.chrisbanes.haze.glass.GlassOptics
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.OpticalSizeValue
import dev.chrisbanes.haze.glass.RefractionProfile
import dev.chrisbanes.haze.glass.SurfaceProfile
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalHazeApi::class)
@Composable
fun ButtonPlayPause(
    isPlay: Boolean,
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val glassStyle = remember {
        GlassStyle {
            optics(
                GlassOptics(
                    refractionStrength = 0.75f,
                    refractionHeightFraction = 0.35f,
                    refractionDisplacement = 56.dp,
                    refractionProfile = RefractionProfile.Edge(28.dp),
                    depth = OpticalSizeValue.Fixed(1f),
                    blurRadius = OpticalSizeValue.Fixed(0.dp),
                    refractionDetailIntensity = 0.76f,
                )
            )
            shape(CircleShape)
            edgeShadow(Color.Transparent)
            ambientResponse(0.42f)
            edgeSoftness(0.dp)
            chromaticAberrationStrength(0.04f)
            surfaceProfile(SurfaceProfile.Circle)
            chromaticAberrationMode(ChromaticAberrationMode.Full)
            contrast(0f)
            whitePoint(0f)
            chromaMultiplier(1.05f)
            contentNormalBlend(0.1f)
            fresnelExponent(2.5f)
            tint(Color.Black.copy(alpha = 0.4f))
            specularExponent(0f)
            specularIntensity(0f)
        }
    }
    Box(
        modifier = modifier
            .size(64.dp)
            .hazeGlass(
                input = HazeInput.Sources(hazeState),
                style = glassStyle,
            )
            .clickableNoRipple(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(targetState = isPlay) { targetState ->
            Icon(
                painter = if (targetState) {
                    painterResource(R.drawable.ic_play)
                } else {
                    painterResource(R.drawable.ic_pause)
                },
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSlider(
    progress: Float,
    duration: Int,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp),
    onProgressChange: (Float) -> Unit = {},
    onProgressChangeFinished: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .draggable(state = rememberDraggableState {}, orientation = Orientation.Horizontal)
            .systemGestureExclusion()
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Slider(
            modifier = Modifier.weight(1f),
            value = progress,
            onValueChange = onProgressChange,
            onValueChangeFinished = onProgressChangeFinished,
            valueRange = 0f..1f,
            steps = 250,
            interactionSource = interactionSource,
            track = { SliderTrack(progress = it.value / it.valueRange.endInclusive) },
            thumb = { SliderThumb() },
        )
        SliderTime(
            modifier = Modifier.padding(bottom = 3.dp),
            current = (duration * progress).toInt(),
            total = duration,
        )
    }
}

@Composable
fun ControlsAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        content = content,
    )
}

@Composable
private fun SliderTrack(
    progress: Float,
) {
    Box(
        modifier = Modifier
            .padding(bottom = 2.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .height(4.dp)
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
            .drawWithCache {
                val offset = Offset(x = size.width * progress, y = 0f)
                onDrawBehind {
                    drawRect(color = Color.Gray, topLeft = offset)
                }
            },
    )
}

@Composable
private fun SliderThumb() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(14.dp)
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
    )
}

@Composable
private fun SliderTime(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.bodySmall
    val density = LocalDensity.current
    val timeWidth = remember {
        density.run {
            textMeasurer.measure(text = "88:88", style = style, maxLines = 1).size.width.toDp()
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.width(timeWidth),
            textAlign = TextAlign.End,
            text = current.toTimeString(),
            style = style,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = "/${total.toTimeString()}",
            style = style,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview
@Composable
private fun ButtonPlayPausePreview() {
    SerenityTheme {
        ButtonPlayPause(
            isPlay = true,
            hazeState = rememberHazeState(),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun VideoSliderPreview() {
    SerenityTheme {
        VideoSlider(
            progress = 0.5f,
            duration = 60 * 1000 * 55,
        )
    }
}
