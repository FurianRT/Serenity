package com.furianrt.noteview.internal.ui.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
internal class PageScreenState {
    val focusedTitleIndex: Int? get() = focusedTitleIndexState.value
    private val focusedTitleIndexState = mutableStateOf<Int?>(null)

    fun focusTitle(index: Int?) {
        focusedTitleIndexState.value = index
    }
}

@Composable
internal fun rememberPageScreenState(): PageScreenState = remember {
    PageScreenState()
}
