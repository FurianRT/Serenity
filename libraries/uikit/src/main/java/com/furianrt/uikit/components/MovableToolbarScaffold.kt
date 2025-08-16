package com.furianrt.uikit.components

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.pxToDp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Stable
class MovableToolbarState {

    companion object {
        const val TOOLBAR_SNAP_DURATION = 350
    }

    private var onExpandRequest: (duration: Int) -> Unit = {}

    fun setExpandRequestListener(callback: (duration: Int) -> Unit) {
        onExpandRequest = callback
    }

    fun expand(duration: Int = TOOLBAR_SNAP_DURATION) {
        onExpandRequest(duration)
    }
}

@Composable
fun MovableToolbarScaffold(
    listState: ScrollableState,
    state: MovableToolbarState,
    toolbar: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    enabled: Boolean = true,
    content: @Composable BoxScope.(topPadding: Dp) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var toolbarOffset by rememberSaveable { mutableFloatStateOf(0f) }
    var toolbarHeight by remember { mutableFloatStateOf(0f) }
    var totalScroll by rememberSaveable { mutableFloatStateOf(0f) }
    val toolbarScrollConnection = remember(listState, enabled) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = consumed.y
                totalScroll += delta
                when {
                    !enabled -> scope.launch {
                        AnimationState(toolbarOffset).animateTo(
                            targetValue = 0f,
                            animationSpec = tween(350),
                            block = { toolbarOffset = value },
                        )
                    }

                    delta < 0 -> {
                        toolbarOffset = (toolbarOffset + delta).coerceAtLeast(-toolbarHeight)
                    }

                    delta > 0 -> {
                        toolbarOffset = (toolbarOffset + delta).coerceAtMost(0f)
                    }
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LaunchedEffect(listState.canScrollBackward, listState.canScrollForward) {
        if (!listState.canScrollBackward && !listState.canScrollForward) {
            state.expand()
            totalScroll = 0f
        }
    }

    state.setExpandRequestListener { duration ->
        scope.launch {
            AnimationState(toolbarOffset).animateTo(
                targetValue = 0f,
                animationSpec = tween(duration),
                block = { toolbarOffset = value },
            )
        }
    }

    val hazeState = remember { HazeState() }

    LaunchedEffect(listState.isScrollInProgress) {
        var forceShowToolbar = false
        when (listState) {
            is LazyGridState -> {
                val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                val firstVisibleItemIndex = visibleItemsInfo.minOfOrNull { it.index }
                val scrollOffset = listState.firstVisibleItemScrollOffset
                forceShowToolbar = scrollOffset <= toolbarHeight && firstVisibleItemIndex == 0
            }
            is LazyListState -> {
                val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                val firstVisibleItemIndex = visibleItemsInfo.minOfOrNull(LazyListItemInfo::index)
                val scrollOffset = listState.firstVisibleItemScrollOffset
                forceShowToolbar = scrollOffset <= toolbarHeight && firstVisibleItemIndex == 0
            }
            is ScrollState -> {
                forceShowToolbar = listState.value <= toolbarHeight
            }
        }
        val isToolbarInHalfState = toolbarOffset != 0f && toolbarOffset != -toolbarHeight
        when {
            !isToolbarInHalfState || listState.isScrollInProgress -> {}

            toolbarOffset > -toolbarHeight / 2 || forceShowToolbar || !listState.canScrollBackward -> {
                AnimationState(toolbarOffset).animateTo(
                    targetValue = 0f,
                    animationSpec = tween(MovableToolbarState.TOOLBAR_SNAP_DURATION),
                    block = { toolbarOffset = value },
                )
            }

            toolbarOffset <= -toolbarHeight / 2 -> {
                AnimationState(toolbarOffset).animateTo(
                    targetValue = -toolbarHeight,
                    animationSpec = tween(MovableToolbarState.TOOLBAR_SNAP_DURATION),
                    block = { toolbarOffset = value },
                )
            }
        }
    }

    val showShadow by remember(listState) {
        derivedStateOf {
            (totalScroll - toolbarOffset).absoluteValue > 1 &&
                    listState.canScrollBackward ||
                    (toolbarOffset == 0f && listState.canScrollBackward)
        }
    }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    Box(modifier = modifier.nestedScroll(toolbarScrollConnection)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
                .graphicsLayer { translationY = toolbarOffset }
                .onSizeChanged { toolbarHeight = it.height.toFloat() }
                .background(MaterialTheme.colorScheme.surface)
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = background,
                        tint = HazeTint(background.copy(alpha = 0.7f)),
                        noiseFactor = 0f,
                        blurRadius = 12.dp,
                    )
                )
                .drawBehind {
                    if (showShadow) {
                        drawBottomShadow(color = shadowColor)
                    }
                }
                .clickableNoRipple {},
            content = { toolbar() },
        )
        Box(
            modifier = Modifier.hazeSource(hazeState),
            content = { content(toolbarHeight.pxToDp()) },
        )
    }
}