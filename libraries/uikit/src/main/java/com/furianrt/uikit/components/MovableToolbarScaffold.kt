package com.furianrt.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.fadingBottomEdge
import com.furianrt.uikit.extensions.pxToDp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
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
    blurRadius: Dp = 12.dp,
    blurAlpha: Float = 0.5f,
    dimSurface: Boolean = false,
    onDimClick: () -> Unit = {},
    content: @Composable BoxScope.(topPadding: Dp) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var toolbarOffset by rememberSaveable { mutableFloatStateOf(0f) }
    var toolbarHeight by rememberSaveable { mutableFloatStateOf(0f) }
    var totalScroll by rememberSaveable(listState) { mutableFloatStateOf(0f) }
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

    SkipFirstEffect(listState.canScrollBackward, listState.canScrollForward) {
        when {
            !listState.canScrollBackward && !listState.canScrollForward -> {
                state.expand()
                totalScroll = 0f
            }

            !listState.canScrollBackward -> totalScroll = 0f
        }
    }

    SideEffect {
        state.setExpandRequestListener { duration ->
            scope.launch {
                AnimationState(toolbarOffset).animateTo(
                    targetValue = 0f,
                    animationSpec = tween(duration),
                    block = { toolbarOffset = value },
                )
            }
        }
    }

    val hazeState = rememberHazeState()

    LaunchedEffect(listState, listState.isScrollInProgress) {
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
            !enabled || !isToolbarInHalfState || listState.isScrollInProgress -> {}

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
            (totalScroll - toolbarOffset).absoluteValue > 5 &&
                    listState.canScrollBackward ||
                    (toolbarOffset == 0f && listState.canScrollBackward)
        }
    }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    Box(modifier = modifier.nestedScroll(toolbarScrollConnection)) {
        Box(
            modifier = Modifier.hazeSource(hazeState),
            content = { content(toolbarHeight.pxToDp()) },
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fadingBottomEdge()
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tint = HazeTint(Color.Transparent),
                        blurRadius = 4.dp,
                    )
                )
                .padding(12.dp)
                .windowInsetsTopHeight(WindowInsets.statusBars),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer { translationY = toolbarOffset }
                .onSizeChanged { toolbarHeight = it.height.toFloat() }
                .clickableNoRipple {},
        ) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = showShadow,
                enter = EnterTransition.None,
                exit = fadeOut(tween(durationMillis = 200, easing = LinearEasing)),
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .hazeEffect(
                            state = hazeState,
                            style = HazeDefaults.style(
                                backgroundColor = background,
                                tint = HazeTint(background.copy(alpha = blurAlpha)),
                                noiseFactor = 0f,
                                blurRadius = blurRadius,
                            )
                        )
                        .drawBehind {
                            if (showShadow) {
                                drawBottomShadow(color = shadowColor)
                            }
                        }
                )
            }
            toolbar()
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = dimSurface,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.scrim)
                    .statusBarsPadding()
                    .height(ToolbarConstants.toolbarHeight)
                    .clickableNoRipple(onClick = onDimClick),
            )
        }
    }
}