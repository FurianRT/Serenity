package com.furianrt.notelist.internal.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.furianrt.notelist.R
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.GlassDefaults
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.OpticalSizeValue
import dev.chrisbanes.haze.glass.RefractionProfile
import dev.chrisbanes.haze.glass.hazeGlass
import com.furianrt.uikit.R as uiR

@OptIn(ExperimentalHazeApi::class)
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
    val colorScheme = MaterialTheme.colorScheme

    val isSelectionActive = selectedNotesCount > 0

    val colorAlphaAnim by animateFloatAsState(
        targetValue = if (isSelectionActive) 0.4f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )

    Row(
        modifier = modifier
            .height(ToolbarConstants.bigToolbarHeight)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ActionButton(
            modifier = Modifier.systemGestureExclusion(),
            icon = if (isSelectionActive) uiR.drawable.ic_exit else uiR.drawable.ic_pie_chart,
            hazeState = hazeState,
            onClick = if (isSelectionActive) onCloseSelectionClick else onStatsClick,
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .then(
                        if (isSelectionActive) {
                            Modifier.hazeGlass(
                                input = HazeInput.Sources(hazeState),
                                performanceMode = HazePerformanceMode.Performance,
                                style = LocalGlassStyle.current.then {
                                    shape(CircleShape)
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
                        } else {
                            Modifier
                                .hazeBlur(
                                    input = HazeInput.Sources(hazeState),
                                    performanceMode = HazePerformanceMode.Performance,
                                    style = HazeBlurStyle {
                                        blurRadius(8.dp)
                                        noiseFactor(0f)
                                        colorEffects(
                                            listOf(
                                                HazeColorEffect.tint(
                                                    colorScheme.surface.copy(alpha = 0.4f),
                                                ),
                                            ),
                                        )
                                    },
                                )
                        }
                    )
                    .background(
                        MaterialTheme.colorScheme.background.copy(
                            alpha = MaterialTheme.colorScheme.background.alpha * colorAlphaAnim,
                        )
                    )
                    .animateContentSize(
                        alignment = Alignment.Center,
                        animationSpec = spring(
                            stiffness = 500f,
                            visibilityThreshold = IntSize.VisibilityThreshold,
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isSelectionActive) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        text = "$selectedNotesCount/$notesCount",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.6f)
                            .clickable(onClick = onSearchClick)
                            .padding(vertical = 8.dp),
                        text = stringResource(R.string.notes_list_search_bar_title),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        ActionButton(
            modifier = Modifier.systemGestureExclusion(),
            icon = if (isSelectionActive) uiR.drawable.ic_delete else uiR.drawable.ic_profile,
            hazeState = hazeState,
            onClick = if (isSelectionActive) onDeleteClick else onSettingsClick,
        )
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun ActionButton(
    @DrawableRes icon: Int,
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
        AnimatedContent(
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
            targetState = icon,
            transitionSpec = { scaleIn().togetherWith(scaleOut() + fadeOut()) },
        ) { targetState ->
            Icon(
                painter = painterResource(targetState),
                contentDescription = null,
            )
        }
    }
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
