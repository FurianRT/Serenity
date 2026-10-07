package com.furianrt.statistics.internal.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.furianrt.statistics.R
import com.furianrt.statistics.internal.ui.entities.MoodStats
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.extensions.toDateString
import com.furianrt.uikit.theme.LocalIsLightTheme
import com.furianrt.uikit.theme.LocalSerenityPlus
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
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.math.min
import com.furianrt.uikit.R as uiR

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun MoodInsightsBlock(
    stats: MoodStats,
    hazeState: HazeState,
    onSerenityPlusClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
) {
    val glassStyle = LocalGlassStyle.current
    val hasSerenityPlus = LocalSerenityPlus.current

    Column(
        modifier = modifier
            .clip(shape)
            .hazeGlass(
                input = HazeInput.Sources(hazeState),
                style = remember(glassStyle) {
                    glassStyle.then {
                        tint(Color.Transparent)
                        shape(shape)
                        whitePoint(0.06f)
                    }
                },
            )
            .padding(top = 12.dp)
            .animateContentSize(),
    ) {
        Title(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        )
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .applyIf(!hasSerenityPlus) {
                        Modifier
                            .blur(12.dp)
                            .clickableNoRipple(onClick = onSerenityPlusClick)
                    },
            ) {
                Spacer(Modifier.size(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PieChart(
                        moods = stats.pieChartData,
                        notesCount = stats.notesCount,
                    )
                    MoodLines(
                        modifier = Modifier.weight(1f),
                        moods = stats.pieChartData,
                    )
                }
                Spacer(Modifier.size(16.dp))
                BestDaysOfWeek(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    days = stats.bestDaysOfWeek,
                )
                Spacer(Modifier.size(4.dp))
                if (stats.chartData.isNotEmpty()) {
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
                    MoodChart(
                        chartData = stats.chartData,
                    )
                    Spacer(Modifier.size(8.dp))
                } else {
                    Spacer(Modifier.size(12.dp))
                }
            }
            if (!hasSerenityPlus) {
                SerenityPlusOverlay(
                    modifier = Modifier.matchParentSize(),
                    onClick = onSerenityPlusClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun SerenityPlusOverlay(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickableNoRipple(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = stringResource(R.string.stats_mood_block_description_1),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = stringResource(R.string.stats_mood_block_description_2),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = stringResource(R.string.stats_mood_block_description_3),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        SerenityPlusButton(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            text = stringResource(uiR.string.title_serenity_plus),
            onClick = onClick,
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
    notesCount: Int,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 34.dp,
    pieSize: Dp = 156.dp,
) {
    val colorScheme = MaterialTheme.colorScheme

    val notesCountAnim by animateIntAsState(
        targetValue = notesCount,
        animationSpec = spring(
            stiffness = Spring.StiffnessVeryLow,
            visibilityThreshold = Int.VisibilityThreshold,
        )
    )

    val animatedAngles = moods.map { mood ->
        animateFloatAsState(
            targetValue = 360f * (mood.percent / 100f),
            animationSpec = tween(
                durationMillis = 800,
            ),
        ).value
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(pieSize)) {
            val stroke = strokeWidth.toPx()

            val diameter = min(size.width, size.height) - stroke
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f,
            )

            val arcSize = Size(diameter, diameter)

            var startAngle = -90f

            drawArc(
                color = colorScheme.tertiary,
                startAngle = startAngle,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Butt,
                ),
            )

            animatedAngles.forEachIndexed { index, sweepAngle ->
                if (sweepAngle > 0f) {
                    drawArc(
                        color = moods[index].level.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(
                            width = stroke,
                            cap = StrokeCap.Butt,
                        ),
                    )
                }

                startAngle += sweepAngle
            }
        }

        Column(
            modifier = Modifier
                .width(pieSize - strokeWidth * 2)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = notesCountAnim.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            BasicText(
                text = pluralStringResource(R.plurals.stats_notes_count, notesCount),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                color = MaterialTheme.colorScheme::onSurface,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = MaterialTheme.typography.labelSmall.fontSize * 0.7f,
                    maxFontSize = MaterialTheme.typography.labelSmall.fontSize,
                ),
            )
        }
    }
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
                    maxPercent = moods.maxBy { it.percent }.percent.takeIf { it > 0f } ?: 100f,
                )
            }
        }
    }
}

@Composable
private fun MoodLine(
    mood: MoodStats.Mood,
    maxPercent: Float,
    modifier: Modifier = Modifier,
) {
    val percentAnim by animateFloatAsState(
        targetValue = mood.percent,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
    )
    val maxPercentAnim by animateFloatAsState(
        targetValue = maxPercent,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
    )

    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.labelSmall.copy(
        fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f,
    )
    val percentWidth = remember(mood.percent) {
        textMeasurer.measure(
            text = "${mood.percent}%",
            style = style,
            maxLines = 1
        ).size.width
    }

    val percentWidthAnim by animateFloatAsState(percentWidth.toFloat())

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val lineFullWidth = maxWidth - 32.dp - percentWidthAnim.pxToDp()
        val lineResultWidth = if (maxPercentAnim == 0f) {
            0.dp
        } else {
            (lineFullWidth * (percentAnim / maxPercentAnim))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = mood.level.icon,
                tint = mood.level.color,
                contentDescription = null,
            )
            Spacer(Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(lineResultWidth)
                    .background(
                        color = mood.level.color,
                        shape = RoundedCornerShape(8.dp),
                    )
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "${percentAnim.toInt()}%",
                maxLines = 1,
                style = style,
            )
        }
    }
}

@Composable
private fun MoodChart(
    chartData: List<MoodStats.ChartEntry>,
    modifier: Modifier = Modifier,
    minPixelDistance: Float = 25f,
) {
    if (chartData.isEmpty()) return

    val startDate = remember(chartData) {
        if (LocalDate.now().year == chartData.first().date.year) {
            chartData.first().date.toDateString("dd LLL")
        } else {
            chartData.first().date.toDateString()
        }
    }

    val endDate = remember(chartData) {
        if (LocalDate.now().year == chartData.last().date.year) {
            chartData.last().date.toDateString("dd LLL")
        } else {
            chartData.last().date.toDateString()
        }
    }

    val lineColor = MaterialTheme.colorScheme.primaryContainer

    val (minDay, totalDaysRange) = remember(chartData) {
        val days = chartData.map { it.date.toEpochSecond() }
        val min = days.minOrNull() ?: 0L
        val max = days.maxOrNull() ?: 0L
        val range = (max - min).coerceAtLeast(1L)
        min to range
    }

    val animatedMoods = chartData.map { point ->
        animateFloatAsState(
            targetValue = point.averageMood,
            animationSpec = tween(durationMillis = 500),
        ).value
    }

    val minMood = 0f
    val maxMood = 5f
    val moodRange = maxMood - minMood

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clipToBounds()
        ) {
            val width = size.width
            val height = size.height

            val verticalPadding = 12.dp.toPx()
            val usableHeight = height - (verticalPadding * 2)

            val rawPoints = chartData.mapIndexed { index, entry ->
                val currentDayOffset = entry.date.toEpochSecond() - minDay
                val x = (currentDayOffset.toFloat() / totalDaysRange) * width

                val normalizedY = (animatedMoods[index] - minMood) / moodRange

                val y = height - verticalPadding - (normalizedY * usableHeight)

                Offset(x, y)
            }

            val collapsedPoints = mutableListOf<Offset>()

            if (rawPoints.isNotEmpty()) {
                var currentGroup = mutableListOf<Offset>()
                currentGroup.add(rawPoints.first())

                for (i in 1 until rawPoints.size) {
                    val nextPoint = rawPoints[i]
                    if (nextPoint.x - currentGroup.first().x < minPixelDistance) {
                        currentGroup.add(nextPoint)
                    } else {
                        collapsedPoints.add(averageOfPoints(currentGroup))
                        currentGroup = mutableListOf(nextPoint)
                    }
                }
                if (currentGroup.isNotEmpty()) {
                    collapsedPoints.add(averageOfPoints(currentGroup))
                }
            }

            val strokePath = Path().apply {
                if (collapsedPoints.isNotEmpty()) {
                    moveTo(collapsedPoints.first().x, collapsedPoints.first().y)

                    for (i in 0 until collapsedPoints.size - 1) {
                        val p0 = collapsedPoints[i]
                        val p1 = collapsedPoints[i + 1]

                        val controlX1 = p0.x + (p1.x - p0.x) / 2f
                        val controlY1 = p0.y
                        val controlX2 = p0.x + (p1.x - p0.x) / 2f
                        val controlY2 = p1.y

                        cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                    }
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.4f),
                        lineColor.copy(alpha = 0.02f)
                    ),
                    startY = collapsedPoints.minOfOrNull { it.y } ?: 0f,
                    endY = height
                )
            )

            drawPath(
                path = strokePath,
                color = lineColor,
                style = Stroke(
                    width = 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .alpha(0.5f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = startDate,
                style = MaterialTheme.typography.labelSmall,
                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f,
            )
            Text(
                text = endDate,
                style = MaterialTheme.typography.labelSmall,
                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f,
            )
        }
    }
}

private fun averageOfPoints(points: List<Offset>): Offset {
    if (points.isEmpty()) return Offset.Zero
    var sumX = 0f
    var sumY = 0f
    for (point in points) {
        sumX += point.x
        sumY += point.y
    }
    return Offset(sumX / points.size, sumY / points.size)
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
                        percent = 0f,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.BAD,
                        percent = 10f,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.SAD,
                        percent = 15f,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.NORMAL,
                        percent = 20f,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.GOOD,
                        percent = 25f,
                    ),
                    MoodStats.Mood(
                        level = MoodStats.Level.PERFECT,
                        percent = 25f,
                    ),
                ),
                chartData = buildList {
                    repeat(5) { index ->
                        add(
                            MoodStats.ChartEntry(
                                date = ZonedDateTime.now().plusDays(index.toLong()),
                                averageMood = index + 1f,
                            ),
                        )
                    }
                },
            ),
            hazeState = rememberHazeState(),
            onSerenityPlusClick = {},
        )
    }
}
