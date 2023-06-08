package com.furianrt.serenity.ui

import android.os.Bundle
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.furianrt.uikit.extensions.expand
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

private const val TOOLBAR_EXPAND_DURATION = 450

@Stable
class MainUiState(
    internal val listState: LazyListState,
    internal val toolbarState: CollapsingToolbarScaffoldState,
    val scrollState: HomeScrollState,
) {
    @OptIn(ExperimentalToolbarApi::class)
    suspend fun scrollToTop() {
        coroutineScope {
            scrollState.firstVisibleIndexState.value = 0
            launch { listState.animateScrollToItem(0) }
            launch { toolbarState.toolbarState.expand(TOOLBAR_EXPAND_DURATION) }
            launch { toolbarState.expand() }
        }
    }
}

@Stable
class HomeScrollState(
    initialScrollDirection: ScrollDirection = ScrollDirection.IDLE,
    initialFirstVisibleIndex: Int = 0,
) {
    enum class ScrollDirection {
        UP, DOWN, IDLE
    }

    val scrollDirection: ScrollDirection get() = scrollDirectionState.value
    internal val scrollDirectionState = mutableStateOf(initialScrollDirection)

    val firstVisibleIndex: Int get() = firstVisibleIndexState.value
    internal val firstVisibleIndexState = mutableStateOf(initialFirstVisibleIndex)

    class HomeScrollSaver : Saver<HomeScrollState, Bundle> {
        companion object {
            private const val SCROLL_DIRECTION = "scroll_direction"
            private const val FIRST_VISIBLE_INDEX = "first_visible_index"
        }

        @Suppress("DEPRECATION")
        override fun restore(value: Bundle) = HomeScrollState(
            initialScrollDirection = (value.getSerializable(SCROLL_DIRECTION) as ScrollDirection?)
                ?: ScrollDirection.IDLE,
            initialFirstVisibleIndex = value.getInt(FIRST_VISIBLE_INDEX, 0),
        )

        override fun SaverScope.save(value: HomeScrollState) = Bundle().apply {
            putSerializable(SCROLL_DIRECTION, value.scrollDirection)
            putInt(FIRST_VISIBLE_INDEX, value.firstVisibleIndex)
        }
    }
}

@Composable
fun rememberHomeScrollState(): HomeScrollState =
    rememberSaveable(saver = HomeScrollState.HomeScrollSaver()) {
        HomeScrollState()
    }

@Composable
fun rememberHomeState(): MainUiState {
    val listState = rememberLazyListState()
    val toolbarState = rememberCollapsingToolbarScaffoldState()
    val homeScrollState = rememberHomeScrollState()
    return remember { MainUiState(listState, toolbarState, homeScrollState) }
}
