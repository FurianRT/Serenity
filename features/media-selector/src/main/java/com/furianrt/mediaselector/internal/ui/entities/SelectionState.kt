package com.furianrt.mediaselector.internal.ui.entities

internal sealed interface SelectionState {
    data object Single : SelectionState
    data class Counter(val order: Int) : SelectionState
    data object Default : SelectionState
}