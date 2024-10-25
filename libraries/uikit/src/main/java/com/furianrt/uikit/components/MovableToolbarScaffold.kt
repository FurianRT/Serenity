package com.furianrt.uikit.components

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.getStatusBarHeight
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private const val TOOLBAR_SNAP_DURATION = 350

@Stable
class MovableToolbarState {

    private var onExpandRequest: () -> Unit = {}

    fun setExpandRequestListener(callback: () -> Unit) {
        onExpandRequest = callback
    }

    fun expand() {
        onExpandRequest()
    }
}

@Composable
fun MovableToolbarScaffold(
    listState: LazyListState,
    toolbar: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    state: MovableToolbarState = MovableToolbarState(),
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val view = LocalView.current
    val statusBarHeight = remember { view.getStatusBarHeight() }
    var toolbarOffset by rememberSaveable { mutableFloatStateOf(0f) }
    val toolbarHeight = LocalDensity.current.run { ToolbarConstants.toolbarHeight.toPx() }
    val toolbarMaxScroll = toolbarHeight + statusBarHeight
    val toolbarScrollConnection = remember(listState, enabled) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = consumed.y
                when {
                    !enabled -> Unit

                    delta < 0 -> {
                        toolbarOffset = (toolbarOffset + delta).coerceAtLeast(-toolbarMaxScroll)
                    }

                    delta > 0 -> {
                        toolbarOffset = (toolbarOffset + delta).coerceAtMost(0f)
                    }
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    val isListAtTop by remember(listState) {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    val scope = rememberCoroutineScope()

    state.setExpandRequestListener {
        scope.launch {
            AnimationState(toolbarOffset).animateTo(
                targetValue = 0f,
                animationSpec = tween(TOOLBAR_SNAP_DURATION),
                block = { toolbarOffset = value },
            )
        }
    }

    val hazeState = remember { HazeState() }

    LaunchedEffect(listState.isScrollInProgress) {
        val forceShowToolbar = listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset <= toolbarHeight
        val isToolbarInHalfState = toolbarOffset != 0f && toolbarOffset != -toolbarMaxScroll
        when {
            !isToolbarInHalfState || listState.isScrollInProgress -> {
                return@LaunchedEffect
            }

            toolbarOffset > -toolbarMaxScroll / 2 || forceShowToolbar || isListAtTop -> {
                AnimationState(toolbarOffset).animateTo(
                    targetValue = 0f,
                    animationSpec = tween(TOOLBAR_SNAP_DURATION),
                    block = { toolbarOffset = value },
                )
            }

            toolbarOffset <= -toolbarMaxScroll / 2 -> {
                AnimationState(toolbarOffset).animateTo(
                    targetValue = -toolbarMaxScroll,
                    animationSpec = tween(TOOLBAR_SNAP_DURATION),
                    block = { toolbarOffset = value },
                )
            }
        }
    }

    val showShadow by remember {
        derivedStateOf {
            val offset = toolbarOffset.absoluteValue.fastRoundToInt()
            listState.firstVisibleItemIndex != 0 ||
                    listState.firstVisibleItemScrollOffset - offset > 1
        }
    }

    Box(modifier = modifier.nestedScroll(toolbarScrollConnection)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
                .graphicsLayer { translationY = toolbarOffset }
                .hazeChild(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tint = HazeTint.Color(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        ),
                        noiseFactor = 0f,
                        blurRadius = 12.dp,
                    ),
                )
                .drawBehind {
                    if (showShadow) {
                        drawBottomShadow(elevation = 8.dp)
                    }
                },
            content = { toolbar() },
        )
        Box(
            modifier = Modifier.haze(hazeState),
            content = { content() },
        )
    }
}