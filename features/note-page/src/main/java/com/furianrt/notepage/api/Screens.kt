package com.furianrt.notepage.api

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
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
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notepage.internal.ui.page.NotePageScreenInternal
import com.furianrt.uikit.utils.DialogIdentifier

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class PageScreenState(
    val listState: ScrollState,
    val bottomScaffoldState: BottomSheetScaffoldState,
    hasContentChanged: Boolean,
) {
    val bottomSheetState: SheetState
        get() = bottomScaffoldState.bottomSheetState

    val hasContentChanged: Boolean
        get() = hasContentChangedState

    val dimSurface: Boolean
        get() = isBottomSheetVisible || isVoiceRecordActive

    var isVoiceRecordActive by mutableStateOf(false)
        internal set

    private var hasContentChangedState by mutableStateOf(hasContentChanged)

    private val isBottomSheetVisible: Boolean
        get() = bottomSheetState.isVisible ||
                bottomSheetState.targetValue == SheetValue.Expanded

    fun setContentChanged(changed: Boolean) {
        hasContentChangedState = changed
    }

    companion object {
        fun saver(
            listState: ScrollState,
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
    listState: ScrollState = rememberScrollState(),
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
    onBackgroundChanged: (background: UiNoteBackground?) -> Unit,
    onTitleFocused: () -> Unit,
    onLocationClick: () -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    openMediaViewScreen: (noteId: String, mediaId: String, identifier: DialogIdentifier) -> Unit,
    openMediaSortingScreen: (noteId: String, blockId: String, identifier: DialogIdentifier) -> Unit,
) {
    NotePageScreenInternal(
        state = state,
        noteId = noteId,
        isSelected = isSelected,
        isInEditMode = isInEditMode,
        isNoteCreationMode = isNoteCreationMode,
        onBackgroundChanged = onBackgroundChanged,
        onTitleFocused = onTitleFocused,
        onLocationClick = onLocationClick,
        openMediaViewer = openMediaViewer,
        openMediaViewScreen = openMediaViewScreen,
        openMediaSortingScreen = openMediaSortingScreen,
    )
}