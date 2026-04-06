package com.furianrt.mediaselector.api

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorBottomSheetInternal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMediaSelectorState(): MediaSelectorState {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    )
    return remember { MediaSelectorState(scaffoldState) }
}

@OptIn(ExperimentalMaterial3Api::class)
class MediaSelectorState internal constructor(
    internal val scaffoldState: BottomSheetScaffoldState,
) {
    internal var params: Params? by mutableStateOf(null)
    internal val bottomSheetState: SheetState
        get() = scaffoldState.bottomSheetState

    val isVisible: Boolean
        get() = bottomSheetState.isVisible || bottomSheetState.targetValue == SheetValue.Expanded

    suspend fun expand(params: Params) {
        this.params = params
        scaffoldState.bottomSheetState.expand()
    }

    suspend fun collapse() {
        scaffoldState.bottomSheetState.hide()
    }

    data class Params(
        val singleChoice: Boolean = false,
        val allowVideo: Boolean = true,
        val onMediaSelected: suspend (result: MediaResult) -> Unit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSelectorBottomSheet(
    state: MediaSelectorState,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) {
    MediaSelectorBottomSheetInternal(
        state = state,
        openMediaViewer = openMediaViewer,
        modifier = modifier,
        content = content,
    )
}