package com.furianrt.uikit.entities

import androidx.compose.runtime.Immutable

@Immutable
data class UiNote(
    val id: String,
    val time: Long,
    val title: String,
)
