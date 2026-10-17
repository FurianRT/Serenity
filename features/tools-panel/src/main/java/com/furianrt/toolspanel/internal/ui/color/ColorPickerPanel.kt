package com.furianrt.toolspanel.internal.ui.color

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import codes.side.color.AnalogousCategory
import codes.side.color.ColorChannel
import codes.side.color.ColorSpace
import codes.side.color.Hsv
import codes.side.colorpicker.material3.AlphaSlider
import codes.side.colorpicker.material3.ChannelPlane
import codes.side.colorpicker.material3.ChannelSlider
import codes.side.colorpicker.material3.ColorPickerDefaults
import codes.side.colorpicker.state.ColorPickerState

@Composable
internal fun ColorPickerPanel(
    modifier: Modifier = Modifier,
) {

}

@Composable
internal fun ColorPickerPanelContent(
    state: ColorPickerState,
    modifier: Modifier = Modifier,
) {
    val axes = remember { planeAxes(Hsv) }
    val planeDimensions = ColorPickerDefaults.currentDimensions()
    Column(
        modifier = modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp),
    ) {
        ChannelPlane(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = state,
            x = axes.first,
            y = axes.second,
            dimensions = planeDimensions,
            thumb = {
                ColorPickerDefaults.PlaneThumb(
                    modifier = Modifier.systemGestureExclusion(),
                    interactionSource = this.interactionSource,
                    diameter = planeDimensions.planeThumbSize,
                )
            },
        )
        Spacer(Modifier.size(8.dp))
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
        Spacer(Modifier.size(4.dp))
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

private fun planeAxes(space: ColorSpace): Pair<ColorChannel, ColorChannel> {
    val others = space.channels.filter { !it.isHue }
    val x = others.firstOrNull { it.analogous == AnalogousCategory.Colorfulness } ?: others.first()
    return x to others.first { it !== x }
}
