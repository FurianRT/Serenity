package com.furianrt.notelist.internal.ui

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.extensions.visibleItemsInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val ITEM_VISIBILITY_THRESHOLD = 90f
private const val ANIMATED_SCROLL_INDEX = 10

@Stable
internal class NoteListScreenState(
    val listState: LazyListState,
    val toolbarState: MovableToolbarState,
) {
    suspend fun scrollToTop() = coroutineScope {
        if (listState.firstVisibleItemIndex > ANIMATED_SCROLL_INDEX) {
            listState.scrollToItem(ANIMATED_SCROLL_INDEX)
        }
        launch { listState.animateScrollToItem(0) }
        toolbarState.expand()
    }

    suspend fun scrollToPosition(position: Int) {
        val visibleIndexes = listState
            .visibleItemsInfo(ITEM_VISIBILITY_THRESHOLD)
            .map(LazyListItemInfo::index)
        if (position !in visibleIndexes) {
            listState.scrollToItem(position)
            toolbarState.expand()
        }
    }
}

@Composable
internal fun rememberMainState(): NoteListScreenState {
    val listState = rememberLazyListState()
    val toolbarState = remember { MovableToolbarState() }
    return remember { NoteListScreenState(listState, toolbarState) }
}
