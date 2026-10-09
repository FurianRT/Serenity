package com.furianrt.notelist.internal.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.GlassDefaults
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.OpticalSizeValue
import dev.chrisbanes.haze.glass.RefractionProfile
import dev.chrisbanes.haze.glass.hazeGlass
import com.furianrt.uikit.R as uiR

@Composable
internal fun Toolbar(
    notesCount: Int,
    selectedNotesCount: Int,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onCloseSelectionClick: () -> Unit = {},
) {
    Crossfade(
        modifier = modifier
            .height(ToolbarConstants.bigToolbarHeight)
            .fillMaxWidth()
            .clickableNoRipple {}
            .systemGestureExclusion(),
        targetState = selectedNotesCount > 0,
    ) { targetState ->
        if (targetState) {
            SelectedContent(
                notesCount = notesCount,
                selectedNotesCount = selectedNotesCount.coerceAtLeast(1),
                hazeState = hazeState,
                onDeleteClick = onDeleteClick,
                onCloseSelectionClick = onCloseSelectionClick,
            )
        } else {
            UnselectedContent(
                hazeState = hazeState,
                onSettingsClick = onSettingsClick,
                onStatsClick = onStatsClick,
                onSearchClick = onSearchClick,
            )
        }
    }
}

@Composable
private fun SelectedContent(
    notesCount: Int,
    selectedNotesCount: Int,
    hazeState: HazeState,
    onDeleteClick: () -> Unit,
    onCloseSelectionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ActionButton(
            icon = painterResource(uiR.drawable.ic_exit),
            hazeState = hazeState,
            onClick = onCloseSelectionClick,
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            NotesCounter(
                total = notesCount,
                selected = selectedNotesCount,
                hazeState = hazeState,
            )
        }
        ActionButton(
            icon = painterResource(uiR.drawable.ic_delete),
            hazeState = hazeState,
            onClick = onDeleteClick,
        )
    }
}

@Composable
private fun UnselectedContent(
    hazeState: HazeState,
    onSettingsClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ActionButton(
            icon = painterResource(uiR.drawable.ic_pie_chart),
            hazeState = hazeState,
            onClick = onStatsClick,
        )
        SearchBar(
            modifier = Modifier.weight(1f),
            hazeState = hazeState,
            onClick = onSearchClick,
        )
        ActionButton(
            icon = painterResource(uiR.drawable.ic_profile),
            hazeState = hazeState,
            onClick = onSettingsClick,
        )
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun ActionButton(
    icon: Painter,
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    IconButton(
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier
                .hazeGlass(
                    input = HazeInput.Sources(hazeState),
                    performanceMode = HazePerformanceMode.Performance,
                    style = LocalGlassStyle.current.then {
                        shape(CircleShape)
                        whitePoint(0.05f)
                        tint(colorScheme.surface.copy(alpha = 0.4f))
                        optics(
                            GlassDefaults.optics.copy(
                                depth = OpticalSizeValue.Fixed(1f),
                                blurRadius = OpticalSizeValue.Fixed(0.dp),
                            )
                        )
                    },
                )
                .padding(8.dp),
            painter = icon,
            contentDescription = null,
        )
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun NotesCounter(
    total: Int,
    selected: Int,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        modifier = modifier
            .hazeGlass(
                input = HazeInput.Sources(hazeState),
                performanceMode = HazePerformanceMode.Performance,
                style = LocalGlassStyle.current.then {
                    shape(CircleShape)
                    whitePoint(0.05f)
                    tint(colorScheme.surface.copy(alpha = 0.4f))
                    optics(
                        GlassDefaults.optics.copy(
                            refractionProfile = RefractionProfile.Edge(10.dp),
                            refractionStrength = 0.65f,
                            depth = OpticalSizeValue.Fixed(1f),
                            blurRadius = OpticalSizeValue.Fixed(2.dp),
                            refractionHeightFraction = 0.25f,
                        )
                    )
                },
            )
            .padding(horizontal = 20.dp, vertical = 4.dp),
        text = "$selected/$total",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
    )
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Toolbar(
            notesCount = 6,
            selectedNotesCount = 0,
            hazeState = HazeState(),
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
            hazeState = HazeState(),
        )
    }
}
