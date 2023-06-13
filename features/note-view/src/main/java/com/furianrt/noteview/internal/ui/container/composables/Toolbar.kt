package com.furianrt.noteview.internal.ui.container.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.assistant.R
import com.furianrt.assistant.api.AssistantLogo
import com.furianrt.noteview.internal.ui.container.ContainerEvent
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import me.onebone.toolbar.CollapsingToolbarScope
import com.furianrt.uikit.R as uiR

private const val LABEL_BUTTON_EDIT = "button_edit"
private const val ANIM_BUTTON_EDIT_DURATION = 350

@Composable
internal fun CollapsingToolbarScope.Toolbar(
    onEvent: (event: ContainerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .pin()
            .height(64.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                modifier = Modifier.padding(start = 4.dp),
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(id = uiR.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = "30 Sep 2022",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonEditAndDone(
                modifier = Modifier
                    .padding(end = 28.dp)
                    .size(24.dp),
                isEditMode = false,
                onClick = {},
            )
            AssistantLogo(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(28.dp),
                onClick = {},
            )
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(id = uiR.drawable.ic_action_menu),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun ButtonEditAndDone(
    onClick: () -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_edit_to_done),
    )

    var targetValue by remember { mutableStateOf(0f) }

    val progress = animateFloatAsState(
        targetValue = targetValue, // if (isEditMode) 0.5f else 1f,
        animationSpec = tween(durationMillis = ANIM_BUTTON_EDIT_DURATION, easing = LinearEasing),
        label = LABEL_BUTTON_EDIT,
    )

    LottieAnimation(
        modifier = modifier.clickableWithScaleAnim(ANIM_BUTTON_EDIT_DURATION) {
            targetValue = if (targetValue == 0f) {
                0.5f
            } else {
                0f
            }
            onClick()
        },
        composition = composition,
        progress = { progress.value },
    )
}
