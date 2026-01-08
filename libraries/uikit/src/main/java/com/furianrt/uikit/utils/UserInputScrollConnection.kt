package com.furianrt.uikit.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

enum class UserScrollState {
    IDLE,
    SCROLLING_UP,
    SCROLLING_DOWN,
}

@Composable
fun rememberUserInputScrollConnection(
    initialState: UserScrollState = UserScrollState.IDLE,
    withIdleState: Boolean = true,
): UserInputScrollConnection = remember(withIdleState) {
    UserInputScrollConnection(initialState, withIdleState)
}

class UserInputScrollConnection internal constructor(
    initialState: UserScrollState,
    private val withIdleState: Boolean,
) : NestedScrollConnection {

    var scrollState by mutableStateOf(initialState)
        private set

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (source == NestedScrollSource.UserInput) {
            scrollState = when {
                available.y > 0f -> UserScrollState.SCROLLING_UP
                available.y < 0f -> UserScrollState.SCROLLING_DOWN
                else -> if (withIdleState) {
                    UserScrollState.IDLE
                } else {
                    scrollState
                }
            }
        }
        return Offset.Zero
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity {
        if (withIdleState) {
            scrollState = UserScrollState.IDLE
        }
        return super.onPostFling(consumed, available)
    }
}
