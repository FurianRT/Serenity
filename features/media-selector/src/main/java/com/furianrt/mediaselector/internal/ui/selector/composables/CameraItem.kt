package com.furianrt.mediaselector.internal.ui.selector.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.internal.ui.entities.Constants
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.R as uiR

@Composable
internal fun CameraItem(
    state: SelectionState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val imageScaleValue by animateFloatAsState(
        targetValue = if (state is SelectionState.Default) 1f else Constants.SELECTED_ITEM_SCALE,
        animationSpec = tween(durationMillis = Constants.IMAGE_SCALE_ANIM_DURATION),
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(2.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = imageScaleValue
                    scaleY = imageScaleValue
                }
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(uiR.drawable.ic_camera),
                tint = Color.White,
                contentDescription = null,
            )
        }
        if (state !is SelectionState.Default) {
            CheckBox(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp),
                state = state,
                onClick = onClick,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSelected() {
    SerenityTheme {
        CameraItem(
            state = SelectionState.Counter(2),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewUnselected() {
    SerenityTheme {
        CameraItem(
            state = SelectionState.Default,
            onClick = {},
        )
    }
}
