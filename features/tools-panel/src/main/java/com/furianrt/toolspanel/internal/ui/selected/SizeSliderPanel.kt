package com.furianrt.toolspanel.internal.ui.selected

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.furianrt.toolspanel.api.ToolsPanelConstants
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlin.math.round
import com.furianrt.uikit.R as uiR

private const val MIN_FONT_SCALE = 0.5f
private const val MAX_FONT_SCALE = 2f
private const val STEPS_COUNT = 29

@Composable
internal fun SizeSliderPanel(
    modifier: Modifier = Modifier,
    onValueChange: (value: Float) -> Unit,
    onCloseClick: () -> Unit,
) {
    var fontMultiplier by remember { mutableFloatStateOf(1f) }

    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.bodyMedium
    val labelWidth = remember(style) {
        textMeasurer.measure(text = "x0.00", style = style, maxLines = 1).size.width + 2
    }

    Row(
        modifier = modifier
            .padding(start = 16.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.width(labelWidth.pxToDp()),
            text = "x$fontMultiplier",
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Visible,
        )
        SizeSlider(
            modifier = Modifier.weight(1f),
            value = fontMultiplier,
            onValueChange = { value ->
                fontMultiplier = value
                onValueChange(value)
            },
        )
        ButtonClose(
            painter = painterResource(uiR.drawable.ic_action_done),
            onClick = onCloseClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SizeSlider(
    value: Float,
    onValueChange: (value: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Slider(
            modifier = modifier.systemGestureExclusion(),
            value = value,
            onValueChange = { newValue ->
                val rounded = round(newValue * 100) / 100
                if (value != rounded) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    onValueChange(rounded)
                }
            },
            valueRange = MIN_FONT_SCALE..MAX_FONT_SCALE,
            steps = STEPS_COUNT,
            track = { state ->
                SliderTrack(
                    progress = (state.value - state.valueRange.start) /
                            (state.valueRange.endInclusive - state.valueRange.start),
                )
            },
            thumb = { SliderThumb() },
        )
    }
}

@Composable
private fun SliderTrack(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.surfaceContainer
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .height(4.dp)
            .background(MaterialTheme.colorScheme.onTertiaryContainer)
            .drawWithCache {
                onDrawBehind {
                    drawRect(color = trackColor, size = size.copy(width = size.width * progress))
                }
            },
    )
}

@Composable
private fun SliderThumb(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(14.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        SizeSliderPanel(
            modifier = Modifier.height(ToolsPanelConstants.PANEL_HEIGHT),
            onCloseClick = {},
            onValueChange = {},
        )
    }
}
