package com.furianrt.notepage.api

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
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
import com.furianrt.notepage.internal.ui.NotePageScreenInternal
import com.furianrt.uikit.utils.DialogIdentifier
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class PageScreenState(
    val listState: LazyListState,
    val titleScrollState: ScrollState,
    val toolbarState: CollapsingToolbarScaffoldState,
    val bottomScaffoldState: BottomSheetScaffoldState,
    hasContentChanged: Boolean,
) {
    val bottomSheetState: SheetState
        get() = bottomScaffoldState.bottomSheetState

    val hasContentChanged: Boolean
        get() = hasContentChangedState

    private var hasContentChangedState by mutableStateOf(hasContentChanged)

    private var onSaveContentRequest: () -> Unit = {}

    private var onFirstTitleFocusRequest: () -> Unit = {}

    fun setOnSaveContentListener(callback: () -> Unit) {
        onSaveContentRequest = callback
    }

    fun saveContent() {
        onSaveContentRequest()
    }

    fun setContentChanged(changed: Boolean) {
        hasContentChangedState = changed
    }

    fun setOnFirstTitleFocusRequestListener(callback: () -> Unit) {
        onFirstTitleFocusRequest = callback
    }

    fun focusFirstTitle() {
        onFirstTitleFocusRequest()
    }

    companion object {
        fun saver(
            listState: LazyListState,
            titleScrollState: ScrollState,
            toolbarState: CollapsingToolbarScaffoldState,
            bottomScaffoldState: BottomSheetScaffoldState,
        ): Saver<PageScreenState, Boolean> = Saver(
            save = { it.hasContentChanged },
            restore = {
                PageScreenState(
                    listState = listState,
                    titleScrollState = titleScrollState,
                    toolbarState = toolbarState,
                    bottomScaffoldState = bottomScaffoldState,
                    hasContentChanged = it,
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberPageScreenState(
    listState: LazyListState = rememberLazyListState(),
    titleScrollState: ScrollState = rememberScrollState(),
    toolbarState: CollapsingToolbarScaffoldState = rememberCollapsingToolbarScaffoldState(),
    bottomScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ),
): PageScreenState = rememberSaveable(
    saver = PageScreenState.saver(
        listState = listState,
        titleScrollState = titleScrollState,
        toolbarState = toolbarState,
        bottomScaffoldState = bottomScaffoldState,
    ),
) {
    PageScreenState(
        listState = listState,
        titleScrollState = titleScrollState,
        toolbarState = toolbarState,
        bottomScaffoldState = bottomScaffoldState,
        hasContentChanged = false,
    )
}

@Composable
fun NotePageScreen(
    state: PageScreenState,
    noteId: String,
    isInEditMode: Boolean,
    isNoteCreationMode: Boolean,
    onFocusChange: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    openMediaViewScreen: (noteId: String, mediaName: String, identifier: DialogIdentifier) -> Unit,
) {
    NotePageScreenInternal(
        state = state,
        noteId = noteId,
        isInEditMode = isInEditMode,
        isNoteCreationMode = isNoteCreationMode,
        onFocusChange = onFocusChange,
        openMediaViewer,
        openMediaViewScreen = openMediaViewScreen,
    )
}