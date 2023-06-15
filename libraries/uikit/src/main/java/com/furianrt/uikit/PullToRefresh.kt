package com.furianrt.uikit

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow

fun Modifier.pullRefresh(
    state: PullRefreshState,
    enabled: Boolean = true,
) = nestedScroll(PullRefreshNestedScrollConnection(state, enabled)).graphicsLayer {
    translationY = state.position
}

private class PullRefreshNestedScrollConnection(
    private val state: PullRefreshState,
    private val enabled: Boolean,
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset = when {
        !enabled -> {
            Offset.Zero
        }

        source == NestedScrollSource.Drag && available.y < 0 -> { // Swiping up
            Offset(0f, state.onPull(available.y))
        }

        else -> Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = when {
        !enabled -> {
            Offset.Zero
        }

        source == NestedScrollSource.Drag && available.y > 0 -> { // Pulling down
            Offset(0f, state.onPull(available.y))
        }

        else -> Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        return Velocity(0f, state.onRelease(available.y))
    }
}

@Composable
fun rememberPullRefreshState(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onThresholdPassed: () -> Unit,
    refreshThreshold: Dp = 80.dp,
): PullRefreshState {
    require(refreshThreshold > 0.dp) { "The refresh trigger must be greater than zero!" }

    val scope = rememberCoroutineScope()
    val onRefreshState = rememberUpdatedState(onRefresh)
    val onThresholdPassedState = rememberUpdatedState(onThresholdPassed)
    val thresholdPx: Float

    with(LocalDensity.current) {
        thresholdPx = refreshThreshold.toPx()
    }

    val state = remember(scope) {
        PullRefreshState(
            animationScope = scope,
            onRefreshState = onRefreshState,
            onThresholdPassedState = onThresholdPassedState,
            threshold = thresholdPx,
        )
    }

    SideEffect {
        state.setRefreshing(refreshing)
        state.setThreshold(thresholdPx)
    }

    return state
}

class PullRefreshState internal constructor(
    private val animationScope: CoroutineScope,
    private val onRefreshState: State<() -> Unit>,
    private val onThresholdPassedState: State<() -> Unit>,
    threshold: Float,
) {
    private val rawProgress get() = adjustedDistancePulled / threshold
    val position get() = _position
    val progress get() = _position / (threshold * thresholdMultiplier)

    private val refreshing get() = _refreshing
    private val threshold get() = _threshold
    private val thresholdMultiplier = 1.8f

    private val dragMultiplier = 0.5f

    private val adjustedDistancePulled by derivedStateOf { distancePulled * dragMultiplier }

    private var _refreshing by mutableStateOf(false)
    private var _position by mutableStateOf(0f)
    private var distancePulled by mutableStateOf(0f)
    private var _threshold by mutableStateOf(threshold)

    private var isThresholdPassed = false

    internal fun onPull(pullDelta: Float): Float {
        if (_refreshing) return 0f

        if (pullDelta <= 0 && adjustedDistancePulled < threshold * thresholdMultiplier) {
            isThresholdPassed = false
        }

        if (!isThresholdPassed && pullDelta > 0 && adjustedDistancePulled > threshold * thresholdMultiplier) {
            onThresholdPassedState.value()
            isThresholdPassed = true
        }

        val newOffset = (distancePulled + pullDelta).coerceAtLeast(0f)
        val dragConsumed = newOffset - distancePulled
        distancePulled = newOffset
        _position = calculateIndicatorPosition()
        return dragConsumed
    }

    internal fun onRelease(velocity: Float): Float {
        if (refreshing) return 0f

        isThresholdPassed = false

        if (adjustedDistancePulled > threshold * 1.4f) {
            onRefreshState.value()
        }
        animateIndicator()
        val consumed = when {
            distancePulled == 0f -> 0f
            velocity < 0f -> 0f
            else -> velocity
        }
        distancePulled = 0f
        return consumed
    }

    internal fun setRefreshing(refreshing: Boolean) {
        if (_refreshing != refreshing) {
            _refreshing = refreshing
            distancePulled = 0f
            animateIndicator()
        }
    }

    internal fun setThreshold(threshold: Float) {
        _threshold = threshold
    }

    private val mutatorMutex = MutatorMutex()

    private fun animateIndicator() = animationScope.launch {
        mutatorMutex.mutate {
            animate(
                initialValue = _position,
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300),
            ) { value, _ ->
                _position = value
            }
        }
    }

    private fun calculateIndicatorPosition(): Float = when {
        adjustedDistancePulled <= threshold * 1.4f -> adjustedDistancePulled
        else -> {
            // How far beyond the threshold pull has gone, as a percentage of the threshold.
            val overshootPercent = abs(rawProgress) - 1.0f
            // Limit the overshoot to 200%. Linear between 0 and 200.
            val linearTension = overshootPercent.coerceIn(0f, 1.5f)
            // Non-linear tension. Increases with linearTension, but at a decreasing rate.
            val tensionPercent = linearTension - linearTension.pow(2) / 4
            // The additional offset beyond the threshold.
            val extraOffset = threshold * tensionPercent
            threshold + extraOffset
        }
    }
}
