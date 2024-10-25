package com.furianrt.mediaselector.api

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorBottomSheetInternal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSelectorBottomSheet(
    state: BottomSheetScaffoldState,
    onMediaSelected: (result: MediaResult) -> Unit,
    openMediaViewer: (route: MediaViewerRoute) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) {
    MediaSelectorBottomSheetInternal(
        state = state,
        onMediaSelected = onMediaSelected,
        openMediaViewer = openMediaViewer,
        modifier = modifier,
        content = content,
    )
}