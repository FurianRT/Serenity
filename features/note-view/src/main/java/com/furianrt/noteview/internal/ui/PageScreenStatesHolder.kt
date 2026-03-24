package com.furianrt.noteview.internal.ui

import androidx.compose.runtime.mutableStateMapOf
import com.furianrt.notepage.api.PageScreenState
import javax.inject.Inject

internal class PageScreenStatesHolder @Inject constructor() {
    val states = mutableStateMapOf<String, PageScreenState>()
}