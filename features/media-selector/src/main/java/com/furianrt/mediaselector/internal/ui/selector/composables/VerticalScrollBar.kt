package com.furianrt.mediaselector.internal.ui.selector.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.R
import com.furianrt.uikit.extensions.dpToPx
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private const val HIDE_LABEL_DELAY = 2000

internal data class ScrollDate(
    val month: String,
    val year: String,
)

@Composable
internal fun VerticalScrollBar(
    columns: Int,
    itemCount: Int,
    verticalSpacing: Dp,
    listState: LazyGridState,
    hazeState: HazeState,
    dateProvider: (itemIndex: Int) -> LocalDate,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val verticalSpacingPx = with(density) { verticalSpacing.toPx() }
    val rowCount = (itemCount + columns - 1) / columns
    var thumbHeight by remember { mutableIntStateOf(0) }

    val rowHeightPx by remember {
        derivedStateOf {
            val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
            (visibleItemsInfo.lastOrNull()?.size?.height ?: 0) + verticalSpacingPx
        }
    }

    val scrollDate by remember(dateProvider) {
        derivedStateOf {
            val date = dateProvider(listState.firstVisibleItemIndex)
            ScrollDate(
                month = date.month
                    .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                year = date.year.toString(),
            )
        }
    }

    val scrollProgress by remember(rowCount, itemCount) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo

            if (visibleItems.isEmpty() || itemCount == 0) return@derivedStateOf 0f

            val firstVisibleRow = listState.firstVisibleItemIndex / columns
            val currentScroll =
                firstVisibleRow * rowHeightPx + listState.firstVisibleItemScrollOffset

            val contentPadding = layoutInfo.beforeContentPadding + layoutInfo.afterContentPadding
            val totalContentHeight = rowCount * rowHeightPx - verticalSpacingPx + contentPadding
            val maxScroll = (totalContentHeight - layoutInfo.viewportSize.height).coerceAtLeast(1f)
            (currentScroll / maxScroll).coerceIn(0f, 1f)
        }
    }

    var dragProgress by rememberSaveable(itemCount) { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        val trackHeightPx = constraints.maxHeight.toFloat()
        val maxThumbOffset = (trackHeightPx - thumbHeight).coerceAtLeast(0f)

        Thumb(
            modifier = Modifier
                .offset {
                    val currentProgress = if (isDragging) dragProgress else scrollProgress
                    IntOffset(x = 0, y = (maxThumbOffset * currentProgress).roundToInt())
                }
                .onSizeChanged { thumbHeight = it.height },
            hazeState = hazeState,
            listState = listState,
            date = scrollDate,
            maxThumbOffset = maxThumbOffset,
            onDragStart = {
                isDragging = true
                onDragStart()
                dragProgress = scrollProgress
            },
            onDragEnd = {
                isDragging = false
                onDragEnd()
            },
            onVerticalDrag = { change, dragAmount ->
                change.consume()

                dragProgress = (dragProgress + dragAmount / maxThumbOffset).coerceIn(0f, 1f)

                val layoutInfo = listState.layoutInfo
                val contentPadding =
                    layoutInfo.beforeContentPadding + layoutInfo.afterContentPadding

                val contentHeight = rowCount * rowHeightPx - verticalSpacingPx + contentPadding

                val maxScroll = (contentHeight - layoutInfo.viewportSize.height).coerceAtLeast(1f)

                val targetScrollPx = dragProgress * maxScroll
                val targetRow = (targetScrollPx / rowHeightPx).toInt().coerceIn(0, rowCount - 1)
                val targetOffset = (targetScrollPx - targetRow * rowHeightPx).roundToInt()

                scope.launch {
                    listState.scrollToItem(
                        index = (targetRow * columns).coerceAtMost(itemCount - 1),
                        scrollOffset = targetOffset
                    )
                }
            },
        )
    }
}

@Composable
private fun Thumb(
    date: ScrollDate,
    maxThumbOffset: Float,
    hazeState: HazeState,
    listState: LazyGridState,
    modifier: Modifier = Modifier,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onVerticalDrag: (change: PointerInputChange, dragAmount: Float) -> Unit,
) {
    val density = LocalDensity.current

    var isDragging by remember { mutableStateOf(false) }
    var showThumb by remember { mutableStateOf(false) }

    val labelOffset by animateDpAsState(targetValue = if (isDragging) 24.dp else 0.dp)

    val isScrollOrDragInProgress by remember {
        derivedStateOf { listState.isScrollInProgress || isDragging }
    }

    LaunchedEffect(isScrollOrDragInProgress) {
        if (isScrollOrDragInProgress) {
            showThumb = true
        } else {
            delay(HIDE_LABEL_DELAY.milliseconds)
            showThumb = false
        }
    }
    AnimatedVisibility(
        modifier = modifier.offset(x = 20.dp),
        visible = showThumb,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DateLabel(
                modifier = Modifier.offset {
                    IntOffset(x = -labelOffset.dpToPx(density).toInt(), y = 0)
                },
                date = date,
                hazeState = hazeState,
            )
            Box(
                modifier = Modifier
                    .pointerInput(maxThumbOffset) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isDragging = true
                                onDragStart()
                            },
                            onVerticalDrag = onVerticalDrag,
                            onDragEnd = {
                                isDragging = false
                                onDragEnd()
                            },
                            onDragCancel = {
                                isDragging = false
                                onDragEnd()
                            },
                        )
                    }
                    .padding(start = 2.dp)
                    .clip(CircleShape)
                    .hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = Color.Black,
                            blurRadius = 16.dp,
                            tint = HazeTint(Color.Black.copy(alpha = 0.4f)),
                        ),
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .systemGestureExclusion(),
            ) {
                Icon(
                    modifier = Modifier
                        .offset(x = (-6).dp)
                        .size(20.dp),
                    painter = painterResource(R.drawable.ic_scrollbar),
                    tint = Color.White,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun DateLabel(
    date: ScrollDate,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val animaDuration = 100
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = Color.Black,
                    blurRadius = 16.dp,
                    tint = HazeTint(Color.Black.copy(alpha = 0.4f)),
                ),
            )
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AnimatedContent(
            targetState = date.month,
            contentAlignment = Alignment.CenterEnd,
            transitionSpec = {
                slideIntoContainer(
                    animationSpec = tween(
                        durationMillis = animaDuration,
                        easing = LinearEasing,
                    ),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                ).togetherWith(
                    slideOutOfContainer(
                        animationSpec = tween(
                            durationMillis = animaDuration,
                            easing = LinearEasing,
                        ),
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    )
                ).using(
                    SizeTransform { _, _ -> snap() }
                )
            },
        ) { targetState ->
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = targetState,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall.copy(
                    letterSpacing = MaterialTheme.typography.bodySmall.letterSpacing * 0.6f,
                ),
            )
        }
        AnimatedContent(
            targetState = date.year,
            contentAlignment = Alignment.CenterEnd,
            transitionSpec = {
                slideIntoContainer(
                    animationSpec = tween(
                        durationMillis = animaDuration,
                        easing = LinearEasing,
                    ),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                ).togetherWith(
                    slideOutOfContainer(
                        animationSpec = tween(
                            durationMillis = animaDuration,
                            easing = LinearEasing,
                        ),
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    )
                ).using(
                    SizeTransform { _, _ -> snap() }
                )
            },
        ) { targetState ->
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = targetState,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall.copy(
                    letterSpacing = MaterialTheme.typography.bodySmall.letterSpacing * 0.6f,
                ),
            )
        }
    }
}

