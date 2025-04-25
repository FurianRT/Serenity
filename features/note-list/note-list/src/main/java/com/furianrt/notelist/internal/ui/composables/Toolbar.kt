package com.furianrt.notelist.internal.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.notelist.R
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

private const val ANIM_BUTTON_SETTINGS_DURATION = 250
private const val ANIM_BUTTON_SETTINGS_ROTATION = 60f

@Composable
internal fun Toolbar(
    notesCount: Int,
    selectedNotesCount: Int,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onCloseSelectionClick: () -> Unit = {},
) {
    Crossfade(
        modifier = modifier
            .statusBarsPadding()
            .height(ToolbarConstants.bigToolbarHeight)
            .fillMaxWidth()
            .systemGestureExclusion(),
        targetState = selectedNotesCount > 0,
    ) { targetState ->
        if (targetState) {
            SelectedContent(
                notesCount = notesCount,
                selectedNotesCount = selectedNotesCount.coerceAtLeast(1),
                onDeleteClick = onDeleteClick,
                onCloseSelectionClick = onCloseSelectionClick,
            )
        } else {
            UnselectedContent(
                onSettingsClick = onSettingsClick,
                onSearchClick = onSearchClick,
            )
        }
    }
}

@Composable
private fun SelectedContent(
    notesCount: Int,
    selectedNotesCount: Int,
    onDeleteClick: () -> Unit,
    onCloseSelectionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
            .padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = modifier
                .minimumInteractiveComponentSize()
                .clickableWithScaleAnim(
                    maxScale = 1.1f,
                    indication = ripple(bounded = false, radius = 20.dp),
                    onClick = onCloseSelectionClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_exit),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "$selectedNotesCount/$notesCount",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = modifier
                .minimumInteractiveComponentSize()
                .clickableWithScaleAnim(
                    maxScale = 1.1f,
                    indication = ripple(bounded = false, radius = 20.dp),
                    onClick = onDeleteClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun UnselectedContent(
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchBar(
            modifier = Modifier
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
            .padding(1.dp)
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
            notesCount = 6,
            selectedNotesCount = 0,
        )
    }
}

@PreviewWithBackground
@Composable
private fun SelectedPreview() {
    SerenityTheme {
        Toolbar(
            notesCount = 6,
            selectedNotesCount = 3,
        )
    }
}
