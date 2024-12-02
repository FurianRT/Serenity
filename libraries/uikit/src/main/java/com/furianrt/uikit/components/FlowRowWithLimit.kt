package com.furianrt.uikit.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun FlowRowWithLimit(
    maxRowsCount: Int,
    modifier: Modifier = Modifier,
    massage: (@Composable () -> Unit)? = null,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        data class FlowContent(
            val placeable: Placeable,
            val x: Int,
            val y: Int,
        )

        val contentMeasurables = subcompose(SubcomposeSlots.CONTENT, content)
        var y = 0
        var x = 0
        var rowMaxY = 0
        var rowsCount = if (contentMeasurables.isEmpty()) {
            0
        } else {
            1
        }
        val flowContents = mutableListOf<FlowContent>()

        val verticalSpacingPx = verticalSpacing.roundToPx()
        val horizontalSpacingPx = horizontalSpacing.roundToPx()

        val placeableConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        for (measurable in contentMeasurables) {
            val placeable = measurable.measure(placeableConstraints)
            if (placeable.width + x > constraints.maxWidth) {
                if (rowsCount == maxRowsCount) {
                    massage ?: break
                    val massageMeasurable =
                        subcompose(SubcomposeSlots.MASSAGE) { massage() }.first()
                    val massagePlaceable = massageMeasurable.measure(placeableConstraints)
                    val suitableX = flowContents
                        .findLast {
                            it.y == y && constraints.maxWidth - it.x >= massagePlaceable.width
                        }
                        ?.x ?: 0

                    flowContents.removeAll { it.y == y && it.x >= suitableX }
                    flowContents.add(FlowContent(massagePlaceable, suitableX, y))
                    rowMaxY = max(massagePlaceable.height + verticalSpacingPx, rowMaxY)
                    break
                }
                x = 0
                y += rowMaxY
                rowMaxY = 0
                rowsCount++
            }
            rowMaxY = max(placeable.height + verticalSpacingPx, rowMaxY)

            flowContents.add(FlowContent(placeable, x, y))
            x += placeable.width + horizontalSpacingPx
        }

        y += rowMaxY

        layout(constraints.maxWidth, max(0, y - verticalSpacingPx)) {
            flowContents.forEach { it.placeable.place(it.x, it.y) }
        }
    }
}

private enum class SubcomposeSlots {
    CONTENT,
    MASSAGE
}