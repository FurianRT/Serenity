package com.furianrt.notelist.internal.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.notelist.R
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.launch

private const val ANIM_BUTTON_SETTINGS_DURATION = 250
private const val ANIM_BUTTON_SETTINGS_ROTATION = 60f

@Composable
internal fun Toolbar(
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .height(ToolbarConstants.bigToolbarHeight)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchBar(
            modifier = Modifier
                .height(34.dp)
                .padding(end = 16.dp)
                .weight(1f),
            onClick = onSearchClick,
        )
        SettingsButton(onClick = onSettingsClick)
    }
}

@Composable
private fun SettingsButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    Icon(
        modifier = modifier
            .graphicsLayer { rotationZ = rotation.value }
            .clickableWithScaleAnim(ANIM_BUTTON_SETTINGS_DURATION) {
                if (rotation.isRunning) {
                    return@clickableWithScaleAnim
                }
                scope.launch {
                    rotation.animateTo(
                        targetValue = rotation.value + ANIM_BUTTON_SETTINGS_ROTATION,
                        animationSpec = tween(ANIM_BUTTON_SETTINGS_DURATION),
                    )
                }
                onClick()
            },
        painter = painterResource(id = R.drawable.ic_settings),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary,
    )
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Toolbar(
            onSettingsClick = {},
            onSearchClick = {},
        )
    }
}
