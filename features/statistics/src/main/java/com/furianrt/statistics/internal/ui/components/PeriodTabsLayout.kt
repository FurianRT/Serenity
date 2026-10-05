package com.furianrt.statistics.internal.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.statistics.R
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun PeriodTabsLayout(
    periods: List<TimePeriod>,
    selectedPeriod: TimePeriod,
    onClick: (period: TimePeriod) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val glassStyle = LocalGlassStyle.current
    val hapticFeedback = LocalHapticFeedback.current

    TabRow(
        modifier = modifier.hazeGlass(
            input = HazeInput.Sources(hazeState),
            style = remember(glassStyle) {
                glassStyle.then {
                    tint(Color.Transparent)
                    shape(RoundedCornerShape(32.dp))
                    whitePoint(0.06f)
                }
            },
        ),
        tabCount = periods.size,
        indicator = { tabPositions ->
            TabIndicator(
                tabPosition = tabPositions[periods.indexOf(selectedPeriod)],
            )
        },
    ) {
        periods.forEach { period ->
            Tab(
                text = period.title,
                isSelected = period == selectedPeriod,
                onClick = {
                    if (period != selectedPeriod) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onClick(period)
                    }
                },
            )
        }
    }
}

@Composable
private fun TabIndicator(
    tabPosition: TabPosition,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .tabIndicatorOffset(
                currentTabPosition = tabPosition,
            )
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(32.dp),
            ),
    )
}

@Composable
private fun TabRow(
    tabCount: Int,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit,
    modifier: Modifier = Modifier,
    minWidth: (index: Int) -> Float? = { null },
    tabs: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier.fillMaxWidth()) { constraints ->
        val totalWidth = constraints.maxWidth
        val averageWidth = totalWidth / tabCount

        val measurables = subcompose(TabSlots.TABS, tabs)

        val tabWidths = IntArray(tabCount)
        var remainingWidth = totalWidth
        var unallocatedTabs = tabCount

        for (i in 0 until tabCount) {
            val customMin = minWidth(i)
            if (customMin != null && customMin > averageWidth) {
                val exactWidth = customMin.toInt().coerceAtMost(remainingWidth)
                tabWidths[i] = exactWidth
                remainingWidth -= exactWidth
                unallocatedTabs--
            }
        }

        if (unallocatedTabs > 0) {
            val baseTabWidth = remainingWidth / unallocatedTabs
            var remainder = remainingWidth % unallocatedTabs

            for (i in 0 until tabCount) {
                if (tabWidths[i] == 0) {
                    val extra = if (remainder > 0) 1 else 0
                    tabWidths[i] = baseTabWidth + extra
                    if (remainder > 0) remainder--
                }
            }
        }

        val tabPlaceables = measurables.mapIndexed { index, measurable ->
            measurable.measure(Constraints.fixedWidth(tabWidths[index]))
        }

        val tabRowHeight = tabPlaceables.maxOfOrNull { it.height } ?: 0

        var currentX = 0
        val tabPositions = List(tabCount) { index ->
            val width = tabWidths[index]
            val position = TabPosition(
                left = currentX.toDp(),
                width = width.toDp()
            )
            currentX += width
            position
        }

        val indicatorPlaceable = subcompose(TabSlots.INDICATOR) {
            indicator(tabPositions)
        }.firstOrNull()?.measure(
            Constraints.fixed(totalWidth, tabRowHeight)
        )

        layout(totalWidth, tabRowHeight) {
            indicatorPlaceable?.place(0, 0)

            var xOffset = 0
            tabPlaceables.forEach { placeable ->
                placeable.placeRelative(x = xOffset, y = 0)
                xOffset += placeable.width
            }
        }
    }
}

@Composable
private fun Tab(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing,
        )
    )
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clickableNoRipple(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
        )
    }
}

private enum class TabSlots {
    TABS,
    INDICATOR,
}

private class TabPosition(
    val left: Dp,
    val width: Dp,
) {
    val right: Dp get() = left + width

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabPosition) return false

        if (left != other.left) return false
        if (width != other.width) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + width.hashCode()
        return result
    }

    override fun toString(): String = "TabPosition(left=$left, right=$right, width=$width)"
}

private class TabIndicatorOffsetNode(
    position: TabPosition,
    private val animationSpec: AnimationSpec<Dp>,
) : Modifier.Node(),
    LayoutModifierNode {

    private val currentTabWidth = Animatable(position.width, typeConverter = Dp.VectorConverter)
    private val indicatorOffset = Animatable(position.left, typeConverter = Dp.VectorConverter)

    fun update(newPosition: TabPosition) {
        coroutineScope.coroutineContext.cancelChildren()
        coroutineScope.launch {
            launch {
                indicatorOffset.animateTo(newPosition.left, animationSpec)
            }
            launch {
                currentTabWidth.animateTo(newPosition.width, animationSpec)
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val width = currentTabWidth.value.roundToPx()
        val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(indicatorOffset.value.roundToPx(), 0)
        }
    }
}

private data class TabIndicatorModifierElement(
    private val position: TabPosition,
    private val animationSpec: AnimationSpec<Dp>,
) : ModifierNodeElement<TabIndicatorOffsetNode>() {
    override fun create(): TabIndicatorOffsetNode = TabIndicatorOffsetNode(position, animationSpec)
    override fun update(node: TabIndicatorOffsetNode) {
        node.update(position)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "tabIndicatorOffset"
        properties["offset"] = position.left
        properties["width"] = position.width
    }
}

private fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    animationSpec: AnimationSpec<Dp> = tween(
        durationMillis = 400,
        easing = FastOutSlowInEasing,
    ),
): Modifier = this then Modifier
    .fillMaxWidth()
    .wrapContentSize(Alignment.BottomStart)
    .then(TabIndicatorModifierElement(currentTabPosition, animationSpec))

private val TimePeriod.title: String
    @Composable get() = when (this) {
        TimePeriod.SEVEN_DAYS -> stringResource(R.string.stats_period_seven_days)
        TimePeriod.ONE_MONTH -> stringResource(R.string.stats_period_one_month)
        TimePeriod.SIX_MONTH -> stringResource(R.string.stats_period_six_month)
        TimePeriod.ONE_YEAR -> stringResource(R.string.stats_period_one_year)
        TimePeriod.ALL_TIME -> stringResource(R.string.stats_period_all_time)
    }

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        PeriodTabsLayout(
            periods = TimePeriod.entries.toList(),
            selectedPeriod = TimePeriod.ONE_MONTH,
            onClick = {},
            hazeState = rememberHazeState(),
        )
    }
}
