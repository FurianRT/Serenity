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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import com.furianrt.serenity.ui.MainScrollState.*
import com.furianrt.uikit.extensions.expand
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

private const val TOOLBAR_EXPAND_DURATION = 450

@Stable
internal class MainScreenState(
    val listState: LazyListState,
    val toolbarState: CollapsingToolbarScaffoldState,
    val scrollConnection: NestedScrollConnection,
    val scrollState: MainScrollState,
) {
    @OptIn(ExperimentalToolbarApi::class)
    suspend fun scrollToTop() {
        coroutineScope {
            launch { listState.animateScrollToItem(0) }
            launch { toolbarState.toolbarState.expand(TOOLBAR_EXPAND_DURATION) }
            launch { toolbarState.expand(TOOLBAR_EXPAND_DURATION) }
        }
    }
}

@Stable
internal class MainScrollState(
    initialScrollDirection: ScrollDirection = ScrollDirection.IDLE,
) {
    enum class ScrollDirection {
        UP, DOWN, IDLE
    }

    val scrollDirection: ScrollDirection get() = scrollDirectionState.value
    internal val scrollDirectionState = mutableStateOf(initialScrollDirection)

    class MainScrollSaver : Saver<MainScrollState, Bundle> {
        companion object {
            private const val SCROLL_DIRECTION = "scroll_direction"
        }

        @Suppress("DEPRECATION")
        override fun restore(value: Bundle) = MainScrollState(
            initialScrollDirection = (value.getSerializable(SCROLL_DIRECTION) as ScrollDirection?)
                ?: ScrollDirection.IDLE,
        )

        override fun SaverScope.save(value: MainScrollState) = Bundle().apply {
            putSerializable(SCROLL_DIRECTION, value.scrollDirection)
        }
    }
}

@Composable
internal fun rememberMainState(): MainScreenState {
    val listState = rememberLazyListState()
    val toolbarState = rememberCollapsingToolbarScaffoldState()
    val scrollState = rememberMainScrollState()
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val scrollSlippage = 3f
                when {
                    available.y > scrollSlippage -> {
                        scrollState.scrollDirectionState.value = ScrollDirection.UP
                    }

                    available.y < -scrollSlippage -> {
                        scrollState.scrollDirectionState.value = ScrollDirection.DOWN
                    }
                }
                return super.onPreScroll(available, source)
            }
        }
    }
    return remember { MainScreenState(listState, toolbarState, scrollConnection, scrollState) }
}

@Composable
private fun rememberMainScrollState(): MainScrollState =
    rememberSaveable(saver = MainScrollSaver()) {
        MainScrollState()
    }
