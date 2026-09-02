package com.furianrt.gallery.internal.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.gallery.R
import com.furianrt.gallery.internal.ui.GalleryEvent
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.rememberHazeState
import com.furianrt.uikit.R as uiR

@Composable
internal fun Toolbar(
    dateFilter: String?,
    hazeState: HazeState,
    onEvent: (event: GalleryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    var tempDateFilter: String? by remember { mutableStateOf(null) }

    LaunchedEffect(dateFilter) {
        if (dateFilter != null) {
            tempDateFilter = dateFilter
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .height(ToolbarConstants.toolbarHeight),
        ) {
            Button(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .systemGestureExclusion(),
                icon = painterResource(uiR.drawable.ic_arrow_back),
                onClick = { onEvent(GalleryEvent.OnCloseClick) },
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.gallery_screen_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Button(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .systemGestureExclusion(),
                icon = painterResource(uiR.drawable.ic_calendar),
                onClick = { onEvent(GalleryEvent.OnDateFiltersClick) },
            )
        }
        AnimatedVisibility(
            visible = dateFilter != null,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            (dateFilter ?: tempDateFilter)?.let { filter ->
                TagItem(
                    modifier = Modifier.padding(start = 20.dp),
                    title = filter,
                    hazeState = hazeState,
                    hazeStyle = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                        noiseFactor = 0f,
                        blurRadius = 8.dp,
                    ),
                    hazeStyleExtraColor = true,
                    isRemovable = true,
                    onClick = { onEvent(GalleryEvent.OnDateFiltersClick) },
                    onRemoveClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onEvent(GalleryEvent.OnRemoveDateFilterClick)
                    },
                )
            }
        }
    }
}

@Composable
private fun Button(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Toolbar(
            dateFilter = "30 Sep 1992 - 22 Feb 2003",
            hazeState = rememberHazeState(),
            onEvent = {},
        )
    }
}
