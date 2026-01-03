package com.furianrt.uikit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastSumBy
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.launch

@Composable
fun TabsSelector(
    tabs: List<String>,
    selectedIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    TabRow(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(32.dp),
            )
            .clickableNoRipple {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                onClick()
            },
        indicator = { tabPositions ->
            TabIndicator(tabPosition = tabPositions[selectedIndex])
        },
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                text = tab,
                isSelected = index == selectedIndex,
            )
        }
    }
}

@Composable
private fun Tab(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            LocalContentColor.current
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )
    Box(
        modifier = modifier
            .sizeIn(minHeight = 40.dp, minWidth = 110.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TabIndicator(
    tabPosition: TabPosition,
) {
    Box(
        modifier = Modifier
            .tabIndicatorOffset(
                currentTabPosition = tabPosition,
                animationSpec = tween(durationMillis = 200),
            )
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(32.dp),
            ),
    )
}

private fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    animationSpec: AnimationSpec<Dp>,
): Modifier = this then Modifier
    .fillMaxWidth()
    .wrapContentSize(Alignment.BottomStart)
    .then(TabIndicatorModifierElement(currentTabPosition, animationSpec))

@Composable
private fun TabRow(
    modifier: Modifier = Modifier,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit,
    tabs: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier.wrapContentSize()) { constraints ->
        var remainingTabWidth = constraints.maxWidth
        val tabPlaceables = subcompose(slotId = TabSlots.TABS, content = tabs)
            .fastMap { tabPlaceable ->
                tabPlaceable.measure(constraints = constraints.copy(maxWidth = remainingTabWidth))
                    .also { remainingTabWidth -= it.width }
            }

        var widthSum = 0
        val tabPositions = tabPlaceables.fastMap { tabPlaceable ->
            TabPosition(
                left = widthSum.toDp(),
                width = tabPlaceable.width.toDp(),
            ).also {
                widthSum += tabPlaceable.width
            }
        }

        val tabRowHeight = tabPlaceables.maxOf(Placeable::height)
        val tabRowWidth = tabPlaceables
            .fastSumBy(Placeable::width)
            .coerceAtMost(constraints.maxWidth)

        val indicatorPlaceable = subcompose(TabSlots.INDICATOR) { indicator(tabPositions) }
            .first()
            .measure(Constraints.fixed(tabRowWidth, tabRowHeight))

        layout(tabRowWidth, tabRowHeight) {
            indicatorPlaceable.placeRelative(0, 0)

            widthSum = 0
            tabPlaceables.fastForEach { placeable ->
                placeable.placeRelative(widthSum, 0)
                widthSum += placeable.width
            }
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

private class TabIndicatorOffsetNode(
    private var position: TabPosition,
    private val animationSpec: AnimationSpec<Dp>,
) : Modifier.Node(),
    LayoutModifierNode {

    private val currentTabWidth = Animatable(position.width, typeConverter = Dp.VectorConverter)
    private val indicatorOffset = Animatable(position.left, typeConverter = Dp.VectorConverter)

    fun update(newPosition: TabPosition) {
        coroutineScope.launch {
            launch {
                if (position.left != newPosition.left) {
                    indicatorOffset.snapTo(position.left)
                    indicatorOffset.animateTo(newPosition.left, animationSpec)
                }
            }
            launch {
                if (position.width != newPosition.width) {
                    currentTabWidth.snapTo(position.width)
                    currentTabWidth.animateTo(newPosition.width, animationSpec)
                }
            }
        }.invokeOnCompletion {
            position = newPosition
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

private data class TabPosition(
    val left: Dp,
    val width: Dp,
)

private enum class TabSlots {
    TABS,
    INDICATOR,
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        TabsSelector(
            tabs = listOf("Dark", "Light"),
            selectedIndex = 0,
            onClick = {},
        )
    }
}
