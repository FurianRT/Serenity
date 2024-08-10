package com.furianrt.serenity.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.furianrt.uikit.extensions.expand
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

private const val TOOLBAR_EXPAND_DURATION = 450

@Stable
internal class MainScreenState(
    val listState: LazyListState,
    val toolbarState: CollapsingToolbarScaffoldState,
) {
    suspend fun scrollToTop() {
        coroutineScope {
            if (listState.firstVisibleItemIndex > 6) {
                listState.scrollToItem(6)
            }
            launch { listState.animateScrollToItem(0) }
            launch { toolbarState.expand(TOOLBAR_EXPAND_DURATION) }
        }
    }
}

@Composable
internal fun rememberMainState(): MainScreenState {
    val listState = rememberLazyListState()
    val toolbarState = rememberCollapsingToolbarScaffoldState()
    return remember { MainScreenState(listState, toolbarState) }
}
