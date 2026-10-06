package com.furianrt.statistics.internal.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.IntSize.Companion
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.furianrt.statistics.R
import com.furianrt.statistics.internal.ui.entities.MoodStats
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.theme.LocalIsLightTheme
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.brighterBy
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.rememberHazeState
import com.furianrt.uikit.R as uiR

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun MoodInsightsBlock(
    stats: MoodStats,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val glassStyle = LocalGlassStyle.current

    Column(
        modifier = modifier
            .hazeGlass(
                input = HazeInput.Sources(hazeState),
                style = remember(glassStyle) {
                    glassStyle.then {
                        tint(Color.Transparent)
                        shape(RoundedCornerShape(16.dp))
                        whitePoint(0.06f)
                    }
                },
            )
            .padding(vertical = 12.dp)
    ) {
        Title(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        )
        Spacer(Modifier.size(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PieChart(
                moods = stats.pieChartData,
            )
            MoodLines(
                modifier = Modifier.weight(1f),
                moods = stats.pieChartData,
            )
        }
        Spacer(Modifier.size(18.dp))
        BestDaysOfWeek(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            days = stats.bestDaysOfWeek,
        )
        Spacer(Modifier.size(10.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .hazeBlur(
                    input = HazeInput.Sources(hazeState),
                    style = HazeBlurStyle {
                        blurRadius(0.dp)
                        noiseFactor(0f)
                        colorEffects(
                            listOf(HazeColorEffect.tint(Color.Transparent)),
                        )
                    },
                )
        )
        Spacer(Modifier.size(10.dp))
        Box(
            Modifier.size(80.dp)
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_smile),
            tint = if (LocalIsLightTheme.current) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer.brighterBy(0.12f)
            },
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.stats_mood_block_title),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun BestDaysOfWeek(
    days: List<String>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                stiffness = Spring.StiffnessMedium,
                visibilityThreshold = IntSize.VisibilityThreshold,
            ),
        ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.stats_mood_block_best_days_of_week_title),
            style = MaterialTheme.typography.labelSmall,
        )
        if (days.isEmpty()) {
            Text(
                text = " --",
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            days.forEachIndexed { index, day ->
                Text(
                    text = if (index != days.lastIndex) "$day," else day,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun PieChart(
    moods: List<MoodStats.Mood>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(140.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape,
            )
    )
}

@Composable
private fun MoodLines(
    moods: List<MoodStats.Mood>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        moods.forEach { mood ->
            key(mood.level) {
                MoodLine(
                    modifier = Modifier.fillMaxWidth(),
                    mood = mood,
                    maxPercent = moods.maxBy { it.percent }.percent.takeIf { it > 0 } ?: 100,
                )
            }
        }
    }
}

@Composable
private fun MoodLine(
    mood: MoodStats.Mood,
    maxPercent: Int,
    modifier: Modifier = Modifier,
) {
    val percentAnim by animateFloatAsState(
        targetValue = mood.percent.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
    )
    val maxPercentAnim by animateFloatAsState(
        targetValue = maxPercent.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
    )

    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.labelSmall.copy(
        fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f,
    )
    val percentWidth = remember(percentAnim) {
        textMeasurer.measure(
            text = "${percentAnim.toInt()}%",
            style = style,
            maxLines = 1
        ).size.width
    }

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val lineFullWidth = maxWidth - 32.dp - percentWidth.pxToDp()
        val lineResultWidth = if (maxPercentAnim == 0f) {
            4.dp
        } else {
            (lineFullWidth * (percentAnim / maxPercentAnim)).coerceAtLeast(4.dp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = mood.level.icon,
                tint = mood.level.color,
                contentDescription = null,
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(lineResultWidth)
                    .background(
                        color = mood.level.color,
                        shape = RoundedCornerShape(8.dp),
                    )
            )
            Text(
                text = "${percentAnim.toInt()}%",
                maxLines = 1,
                style = style,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        MoodInsightsBlock(
            stats = MoodStats(
                bestDaysOfWeek = listOf("Sunday", "Friday"),
                notesCount = 242,
                pieChartData = listOf(
                    MoodStats.Mood(
                        level = MoodStats.Level.TERRIBLE,
                        percent = 0,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.BAD,
                        percent = 10,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.SAD,
                        percent = 15,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.NORMAL,
                        percent = 20,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.GOOD,
                        percent = 25,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.PERFECT,
                        percent = 25,
                    ),
                ),
            ),
            hazeState = rememberHazeState(),
        )
    }
}
