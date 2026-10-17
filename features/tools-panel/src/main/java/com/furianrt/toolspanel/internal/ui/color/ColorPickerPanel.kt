package com.furianrt.toolspanel.internal.ui.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import codes.side.color.Hsv
import codes.side.colorpicker.material3.AlphaSlider
import codes.side.colorpicker.material3.ChannelSlider
import codes.side.colorpicker.material3.ColorPickerDefaults
import codes.side.colorpicker.state.ColorPickerState
import codes.side.colorpicker.state.rememberColorPickerState
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.uikit.extensions.clickableNoRipple
import kotlinx.coroutines.flow.drop
import kotlin.math.max

internal enum class ColorPickerMode {
    FONT_COLOR,
    FONT_FILL_COLOR,
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ColorPickerPanel(
    titleState: NoteTitleState?,
    mode: ColorPickerMode,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val isImeVisible = WindowInsets.isImeVisible
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - density.run { navigationBarsHeight.toDp() }

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
        }
    }

    if (visible || !isImeVisible) {
        AnimatedVisibility(
            modifier = modifier,
            visible = visible,
            enter = if (isImeVisible) {
                EnterTransition.None
            } else {
                expandVertically(expandFrom = Alignment.Top)
            },
            exit = if (isImeVisible) {
                ExitTransition.None
            } else {
                shrinkVertically(shrinkTowards = Alignment.Top)
            },
        ) {
            val colorPickerState = rememberColorPickerState(Color.White)

            LaunchedEffect(mode, titleState, colorPickerState) {
                snapshotFlow { colorPickerState.color }
                    .drop(1)
                    .collect { color ->
                        titleState?.removeSpan(
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                            spanType = when (mode) {
                                ColorPickerMode.FONT_COLOR -> {
                                    NoteTitleState.SpanType.FontColor()
                                }

                                ColorPickerMode.FONT_FILL_COLOR -> {
                                    NoteTitleState.SpanType.FillColor()
                                }
                            },
                        )
                        titleState?.addSpan(
                            start = titleState.selection.min,
                            end = titleState.selection.max,
                            spanType = when (mode) {
                                ColorPickerMode.FONT_COLOR -> {
                                    NoteTitleState.SpanType.FontColor(color)
                                }

                                ColorPickerMode.FONT_FILL_COLOR -> {
                                    NoteTitleState.SpanType.FillColor(color)
                                }
                            },
                        )
                    }
            }
            ColorPickerPanelContent(
                modifier = Modifier
                    .height(contentHeight)
                    .clickableNoRipple(onClick = {}),
                state = colorPickerState,
            )
        }
    }
}

@Composable
internal fun ColorPickerPanelContent(
    state: ColorPickerState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ChannelSlider(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            channel = Hsv.H,
            label = null,
            valueLabel = null,
            semanticLabel = null,
            semanticValueText = null,
            thumb = {
                ColorPickerDefaults.SliderThumb(
                    modifier = Modifier.systemGestureExclusion(),
                    interactionSource = interactionSource,
                    color = thumbColor,
                )
            },
        )
        ChannelSlider(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            channel = Hsv.V,
            label = null,
            valueLabel = null,
            semanticLabel = null,
            semanticValueText = null,
            thumb = {
                ColorPickerDefaults.SliderThumb(
                    modifier = Modifier.systemGestureExclusion(),
                    interactionSource = interactionSource,
                    color = thumbColor,
                )
            },
        )
        AlphaSlider(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            label = null,
            valueLabel = null,
            semanticLabel = null,
            semanticValueText = null,
            thumb = {
                ColorPickerDefaults.SliderThumb(
                    modifier = Modifier.systemGestureExclusion(),
                    interactionSource = interactionSource,
                    color = thumbColor,
                )
            },
        )
    }
}
