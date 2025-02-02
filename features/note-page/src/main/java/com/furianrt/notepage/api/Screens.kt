package com.furianrt.notepage.api

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notepage.internal.ui.page.NotePageScreenInternal
import com.furianrt.uikit.extensions.visibleItemsInfo
import com.furianrt.uikit.utils.DialogIdentifier

private const val ITEM_VISIBILITY_THRESHOLD = 90f

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class PageScreenState(
    val listState: LazyListState,
    val bottomScaffoldState: BottomSheetScaffoldState,
    hasContentChanged: Boolean,
) {
    val bottomSheetState: SheetState
        get() = bottomScaffoldState.bottomSheetState

    val hasContentChanged: Boolean
        get() = hasContentChangedState

    val dimSurface: Boolean
        get() = isBottomSheetVisible || isVoiceRecordActive

    internal var isVoiceRecordActive by mutableStateOf(false)

    private var hasContentChangedState by mutableStateOf(hasContentChanged)

    private val isBottomSheetVisible: Boolean
        get() = bottomSheetState.isVisible ||
                bottomSheetState.targetValue == SheetValue.Expanded

    private var onSaveContentRequest: () -> Unit = {}

    private var onTitleFocusRequest: (index: Int) -> Unit = {}
    private var onBringContentToViewRequest: (index: Int) -> Unit = {}

    fun setOnSaveContentListener(callback: () -> Unit) {
        onSaveContentRequest = callback
    }

    fun saveContent() {
        onSaveContentRequest()
    }

    fun setContentChanged(changed: Boolean) {
        hasContentChangedState = changed
    }

    fun setOnTitleFocusRequestListener(callback: (index: Int) -> Unit) {
        onTitleFocusRequest = callback
    }

    fun focusTitle(index: Int) {
        onTitleFocusRequest(index)
    }

    fun setOnBringContentToViewListener(callback: (index: Int) -> Unit) {
        onBringContentToViewRequest = callback
    }

    fun bringContentToView(index: Int) {
        onBringContentToViewRequest(index)
    }

    suspend fun scrollToPosition(position: Int, topOffset: Int) {
        val visibleIndexes = listState.layoutInfo
            .visibleItemsInfo(ITEM_VISIBILITY_THRESHOLD, topOffset)
            .map(LazyListItemInfo::index)
        if (position !in visibleIndexes) {
            listState.animateScrollToItem(position)
        }
    }

    companion object {
        fun saver(
            listState: LazyListState,
            bottomScaffoldState: BottomSheetScaffoldState,
        ): Saver<PageScreenState, Pair<Boolean, Boolean>> = Saver(
            save = { it.hasContentChanged to it.isVoiceRecordActive },
            restore = {
                PageScreenState(
                    listState = listState,
                    bottomScaffoldState = bottomScaffoldState,
                    hasContentChanged = it.first,
                ).apply {
                    this.isVoiceRecordActive = it.second
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberPageScreenState(
    listState: LazyListState = rememberLazyListState(),
    bottomScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ),
): PageScreenState = rememberSaveable(
    saver = PageScreenState.saver(
        listState = listState,
        bottomScaffoldState = bottomScaffoldState,
    ),
) {
    PageScreenState(
        listState = listState,
        bottomScaffoldState = bottomScaffoldState,
        hasContentChanged = false,
    )
}

@Composable
fun NotePageScreen(
    state: PageScreenState,
    noteId: String,
    isInEditMode: Boolean,
    isSelected: Boolean,
    isNoteCreationMode: Boolean,
    onFocusChange: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
) {
    NotePageScreenInternal(
        state = state,
        noteId = noteId,
        isSelected = isSelected,
        isInEditMode = isInEditMode,
        isNoteCreationMode = isNoteCreationMode,
        onFocusChange = onFocusChange,
        openMediaViewer = openMediaViewer,
        openMediaViewScreen = openMediaViewScreen,
    )
}