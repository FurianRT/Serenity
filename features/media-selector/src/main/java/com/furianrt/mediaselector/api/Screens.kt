package com.furianrt.mediaselector.api

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.furianrt.mediaselector.internal.ui.MediaSelectorBottomSheetInternal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSelectorBottomSheet(
    state: MediaSelectorState,
    modifier: Modifier = Modifier,
) {
    if (state.isVisible) {
        MediaSelectorBottomSheetInternal(
            modifier = modifier,
            state = state.bottomSheetState,
            onDismissRequest = { state.hide() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMediaSelectorState(
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
    isVisible: Boolean = false,
): MediaSelectorState = rememberSaveable(saver = MediaSelectorState.Saver(bottomSheetState)) {
    MediaSelectorState(isVisible, bottomSheetState)
}

@OptIn(ExperimentalMaterial3Api::class)
class MediaSelectorState(
    isInitiallyVisible: Boolean,
    val bottomSheetState: SheetState,
) {
    var isVisible by mutableStateOf(isInitiallyVisible)
        private set

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

    companion object {
        fun Saver(bottomSheetState: SheetState) = Saver<MediaSelectorState, Boolean>(
            save = { it.isVisible },
            restore = { savedValue -> MediaSelectorState(savedValue, bottomSheetState) },
        )
    }
}



