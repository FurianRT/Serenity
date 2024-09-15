package com.furianrt.serenity.ui

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.furianrt.uikit.extensions.expand
import com.furianrt.uikit.extensions.expandWithoutAnim
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

private const val TOOLBAR_EXPAND_DURATION = 450
private const val ANIMATED_SCROLL_INDEX = 10

@Stable
internal class MainScreenState(
    val listState: LazyListState,
    val toolbarState: CollapsingToolbarScaffoldState,
) {
    suspend fun scrollToTop() = coroutineScope {
        if (listState.firstVisibleItemIndex > ANIMATED_SCROLL_INDEX) {
            listState.scrollToItem(ANIMATED_SCROLL_INDEX)
        }
        launch { listState.animateScrollToItem(0) }
        launch { toolbarState.expand(TOOLBAR_EXPAND_DURATION) }
    }

    suspend fun scrollToPosition(position: Int) {
        val visibleIndexes = listState.layoutInfo.visibleItemsInfo.map(LazyListItemInfo::index)
        if (position !in visibleIndexes) {
            toolbarState.expandWithoutAnim()
            listState.scrollToItem(position)
        }
    }
}

@Composable
internal fun rememberMainState(): MainScreenState {
    val listState = rememberLazyListState()
    val toolbarState = rememberCollapsingToolbarScaffoldState()
    return remember { MainScreenState(listState, toolbarState) }
}
