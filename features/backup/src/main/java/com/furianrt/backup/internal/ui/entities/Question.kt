package com.furianrt.backup.internal.ui.entities

internal data class Question(
    val id: String,
    val title: String,
    val answer: String,
    val isExpanded: Boolean,
)